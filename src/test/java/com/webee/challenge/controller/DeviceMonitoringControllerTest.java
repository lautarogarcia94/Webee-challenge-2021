package com.webee.challenge.controller;


import com.google.firebase.FirebaseException;
import com.webee.challenge.controllers.DeviceMonitoringController;
import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;
import com.webee.challenge.services.database.DataBaseService;
import com.webee.challenge.services.marshaller.MarshallerService;
import com.webee.challenge.services.validations.DeviceValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeviceMonitoringControllerTest {

    @Mock
    private DeviceValidationService mockDeviceValidationService;

    @Mock
    private DataBaseService mockDataBaseService;

    @Mock
    private MarshallerService mockMarshallerService;

    @InjectMocks
    private DeviceMonitoringController deviceMonitoringController;

    private List<Device> deviceList;
    private Device device;

    @BeforeEach
    public void setUp() throws FirebaseException, ValidationException {
        initMocks(this);
        populateFields();
        populateMocks();
    }

    private void populateMocks() throws FirebaseException, ValidationException {
        when(mockMarshallerService.marshallDevice(device)).thenReturn("Marshall device");
        when(mockMarshallerService.marshallDeviceList(deviceList)).thenReturn("Marshall device list");
    }

    private void populateFields() {
        device = Device.builder()
                .date("05-10-2021")
                .ID("qwertyuiop")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();
        deviceList = new ArrayList<>();
        deviceList.add(device);
    }

    @Test
    void shouldReturnDeviceListSuccessfullyWith200Status() throws FirebaseException {
        when(mockDataBaseService.searchAllDevices()).thenReturn(deviceList);

        ResponseEntity<String> responseEntity = deviceMonitoringController.getDevices();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Marshall device list", responseEntity.getBody());
    }

    @Test
    void shouldReturnErrorMessageWith500StatusWhenExceptionOccurs() throws FirebaseException {
        when(mockDataBaseService.searchAllDevices()).thenThrow(new FirebaseException("Database exception mock"));

        ResponseEntity<String> responseEntity = deviceMonitoringController.getDevices();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Database exception mock", responseEntity.getBody());
    }

    @Test
    void shouldReturnDeviceSuccessfullyWith200StatusWhileLookingByMac() throws ValidationException, FirebaseException {
        doNothing().when(mockDeviceValidationService).validateMac("Valid MAC");
        when(mockDataBaseService.searchDeviceByMac("Valid MAC")).thenReturn(device);

        ResponseEntity<String> responseEntity = deviceMonitoringController.getDeviceByMac("Valid MAC");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Marshall device", responseEntity.getBody());
    }

    @Test
    void shouldReturnErrorMessageWith400StatusWhileLookingByMacAndValidationExceptionOccurs() throws ValidationException {
        doThrow(new ValidationException("Invalid MAC address")).when(mockDeviceValidationService).validateMac("throw ValidationException");

        ResponseEntity<String> responseEntity = deviceMonitoringController.getDeviceByMac("throw ValidationException");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid MAC address", responseEntity.getBody());
    }

    @Test
    void shouldReturnErrorMessageWith500StatusWhileLookingByMacAndFireBaseExceptionOccurs() throws ValidationException, FirebaseException {
        doNothing().when(mockDeviceValidationService).validateMac("throw FireBaseException");
        when(mockDataBaseService.searchDeviceByMac("throw FireBaseException")).thenThrow(new FirebaseException("Problem with FireBase"));

        ResponseEntity<String> responseEntity = deviceMonitoringController.getDeviceByMac("throw FireBaseException");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Problem with FireBase", responseEntity.getBody());
    }

    @Test
    void shouldReturnDeviceSuccessfullyWith200StatusWhileLookingByID() throws ValidationException, FirebaseException {
        doNothing().when(mockDeviceValidationService).validateId("Valid ID");
        when(mockDataBaseService.searchDeviceById("Valid ID")).thenReturn(device);

        ResponseEntity<String> responseEntity = deviceMonitoringController.getDeviceById("Valid ID");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Marshall device", responseEntity.getBody());
    }

    @Test
    void shouldReturnErrorMessageWith400StatusWhileLookingByIDAndValidationExceptionOccurs() throws ValidationException {
        doThrow(new ValidationException("Invalid ID address")).when(mockDeviceValidationService).validateId("throw ValidationException");

        ResponseEntity<String> responseEntity = deviceMonitoringController.getDeviceById("throw ValidationException");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid ID address", responseEntity.getBody());
    }

    @Test
    void shouldReturnErrorMessageWith500StatusWhileLookingByIDAndFireBaseExceptionOccurs() throws ValidationException, FirebaseException {
        when(mockDataBaseService.searchDeviceById("throw FireBaseException")).thenThrow(new FirebaseException("Problem with FireBase"));
        doNothing().when(mockDeviceValidationService).validateId("throw FireBaseException");

        ResponseEntity<String> responseEntity = deviceMonitoringController.getDeviceById("throw FireBaseException");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Problem with FireBase", responseEntity.getBody());
    }

    @Test
    void shouldRegisterDeviceSuccessfullyWith200Status() throws ValidationException, FirebaseException {
        DeviceRequest deviceRequest = DeviceRequest.builder()
                .date("01-01-2021")
                .macAddress("11:11:11:11:11:11")
                .build();

        doNothing().when(mockDeviceValidationService).validateDeviceRequest(deviceRequest);
        doNothing().when(mockDataBaseService).registerDevice(deviceRequest);

        ResponseEntity<String> responseEntity = deviceMonitoringController.registerDevice(deviceRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Device registered", responseEntity.getBody());
    }

    @Test
    void shouldReturnErrorMessageWith400StatusWhileRegisteringDeviceAndValidationExceptionOccurs() throws ValidationException, FirebaseException {
        DeviceRequest deviceRequest = DeviceRequest.builder()
                .date("01-01-2021")
                .macAddress("FF:FF:FF:11:11:11")
                .build();

        doThrow(new ValidationException("Invalid MAC address")).when(mockDeviceValidationService).validateDeviceRequest(deviceRequest);

        ResponseEntity<String> responseEntity = deviceMonitoringController.registerDevice(deviceRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid MAC address", responseEntity.getBody());
    }

    @Test
    void shouldReturnErrorMessageWith500StatusWhileRegisteringDeviceAndFireBaseExceptionOccurs() throws ValidationException, FirebaseException {
        DeviceRequest deviceRequest = DeviceRequest.builder()
                .date("01-01-2021")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();

        doNothing().when(mockDeviceValidationService).validateDeviceRequest(deviceRequest);
        doThrow(new FirebaseException("Problem with FireBase")).when(mockDataBaseService).registerDevice(deviceRequest);

        ResponseEntity<String> responseEntity = deviceMonitoringController.registerDevice(deviceRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Problem with FireBase", responseEntity.getBody());
    }


    @Test
    void shouldDeleteDeviceSuccessfullyWith200Status() throws ValidationException, FirebaseException {
        doNothing().when(mockDeviceValidationService).validateId("Valid ID");
        doNothing().when(mockDataBaseService).deleteDevice("Valid ID");

        ResponseEntity<String> responseEntity = deviceMonitoringController.deleteDeviceByID("Valid ID");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Device deleted", responseEntity.getBody());
    }

    @Test
    void shouldReturnErrorMessageWith400StatusWhileDeletingDeviceAndValidationExceptionOccurs() throws ValidationException {
        doThrow(new ValidationException("Invalid ID")).when(mockDeviceValidationService).validateId("throw ValidationException");

        ResponseEntity<String> responseEntity = deviceMonitoringController.deleteDeviceByID("throw ValidationException");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid ID", responseEntity.getBody());
    }

    @Test
    void shouldReturnErrorMessageWith500StatusWhileDeletingDeviceAndFireBaseExceptionOccurs() throws ValidationException, FirebaseException {
        doThrow(new FirebaseException("Problem with FireBase")).when(mockDataBaseService).deleteDevice("throw FireBaseException");
        doNothing().when(mockDeviceValidationService).validateId("throw FireBaseException");

        ResponseEntity<String> responseEntity = deviceMonitoringController.deleteDeviceByID("throw FireBaseException");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Problem with FireBase", responseEntity.getBody());
    }
}
