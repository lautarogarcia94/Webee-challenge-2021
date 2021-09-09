package com.webee.challenge.service.marshaller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;
import com.webee.challenge.services.marshaller.impl.MarshallerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MarshallerServiceImplTest {

    @Mock(name = "jsonMarshaller")
    private ObjectMapper mockMapper;

    @InjectMocks
    private MarshallerServiceImpl marshallerService;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    void shouldMarshallDeviceSuccessfully() throws JsonProcessingException {
        Device device = Device.builder()
                .date("05-10-2021")
                .ID("qwertyuiop")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();
        when(mockMapper.writeValueAsString(device)).thenReturn("Device Marshalled OK");

        String result = marshallerService.marshallDevice(device);

        assertEquals("Device Marshalled OK", result);
    }

    @Test
    void shouldReturnNullWhenMarshallerReturnsNullWhileMharshallingDevice() throws JsonProcessingException {
        Device nullDevice = new Device();
        when(mockMapper.writeValueAsString(nullDevice)).thenReturn("null");

        String result = marshallerService.marshallDevice(nullDevice);

        assertNull(result);
    }

    @Test
    void shouldMarshallDeviceRequestSuccessfully() throws JsonProcessingException {
        DeviceRequest deviceRequest = DeviceRequest.builder()
                .date("01-01-2021")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();
        when(mockMapper.writeValueAsString(deviceRequest)).thenReturn("Device Marshalled OK");

        String result = marshallerService.marshallDeviceRequest(deviceRequest);

        assertEquals("Device Marshalled OK", result);
    }

    @Test
    void shouldReturnNullWhenMarshallerReturnsNullWhileMharshallingDeviceRequest() throws JsonProcessingException {
        DeviceRequest nullDeviceRequest = new DeviceRequest();
        when(mockMapper.writeValueAsString(nullDeviceRequest)).thenReturn("null");

        String result = marshallerService.marshallDeviceRequest(nullDeviceRequest);

        assertNull(result);
    }

    @Test
    void shouldMarshallDeviceListSuccessfullyWhenOnlyOneDevice() throws JsonProcessingException {
        Device device = Device.builder()
                .date("05-10-2021")
                .ID("qwertyuiop")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();
        List<Device> deviceList = new ArrayList<>();
        deviceList.add(device);

        when(mockMapper.writeValueAsString(device)).thenReturn("Device Marshalled OK");

        String result = marshallerService.marshallDeviceList(deviceList);

        assertEquals("Device Marshalled OK", result);
    }

    @Test
    void shouldMarshallDeviceListSuccessfullyWhenMoreThanOneDevice() throws JsonProcessingException {
        Device device1 = Device.builder()
                .date("05-10-2021")
                .ID("qwertyuiop")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();
        Device device2 = Device.builder()
                .date("05-10-2021")
                .ID("qwertyuiop")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();
        List<Device> deviceList = new ArrayList<>();
        deviceList.add(device1);
        deviceList.add(device2);

        when(mockMapper.writeValueAsString(device1)).thenReturn("Device1 OK");
        when(mockMapper.writeValueAsString(device1)).thenReturn("Device2 OK");

        String result = marshallerService.marshallDeviceList(deviceList);

        assertEquals("{Device2 OK,Device2 OK}", result);
    }
}