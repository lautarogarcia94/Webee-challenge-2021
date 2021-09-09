package com.webee.challenge.controller;


import com.google.firebase.database.DatabaseException;
import com.webee.challenge.controllers.DeviceMonitoringController;
import com.webee.challenge.model.Device;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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
    public void setUp(){
        initMocks(this);
        populateFields();
        populateMocks();
    }

    private void populateMocks() {
        when(mockDataBaseService.searchAllDevices()).thenReturn(deviceList);
        when(mockMarshallerService.marshallDeviceList(deviceList)).thenReturn("Marshall device list");
    }

    private void populateFields(){
        device = Device.builder()
                .date("05-10-2021")
                .ID("qwertyuiop")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();
        deviceList = new ArrayList<>();
        deviceList.add(device);
    }

    @Test
    void shouldReturnDeviceListSuccessfullyWith200Status(){
        ResponseEntity<String> responseEntity = deviceMonitoringController.getDevices();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Marshall device list", responseEntity.getBody());
    }

    @Test
    void shouldReturnErrorMessageWith500StatusWhenExceptionOccurs(){
        when(mockDataBaseService.searchAllDevices()).thenThrow(new DatabaseException("Database exception mock"));

        ResponseEntity<String> responseEntity = deviceMonitoringController.getDevices();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Database exception mock", responseEntity.getBody());
    }

}
