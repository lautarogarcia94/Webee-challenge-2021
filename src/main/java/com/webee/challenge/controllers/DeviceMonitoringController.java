package com.webee.challenge.controllers;

import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("device-monitoring")
public class DeviceMonitoringController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceMonitoringController.class);

    @GetMapping(path = "/get-devices-list", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Device>> getDevices() {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-devices-list");
        Device device1 = Device.builder()
                .date(LocalDate.now())
                .ID(10)
                .macAddress("MAC-Adress-1")
                .build();

        Device device2 = Device.builder()
                .date(LocalDate.now())
                .ID(20)
                .macAddress("MAC-Adress-2")
                .build();

        List<Device> deviceList = new ArrayList<>();
        deviceList.add(device1);
        deviceList.add(device2);

        return new ResponseEntity<>(deviceList, HttpStatus.OK);
    }

    @GetMapping(path = "/get-device/{deviceInformation}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Device> getDevice(@PathVariable String deviceInformation) {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-device/{}", deviceInformation);

        Device device = Device.builder()
                .date(LocalDate.now())
                .ID(20)
                .macAddress("MAC-Adress-2")
                .build();
        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @PostMapping(path = "/register-device", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> registerDevice(@RequestBody DeviceRequest device) {
        LOG.info("Post request received, device: {}", device);
        return new ResponseEntity<>("device created", HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/delete-device/{deviceID}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> deleteDeviceByID(@PathVariable String deviceID) {
        LOG.info("Delete request received, ID: {}", deviceID);
        return new ResponseEntity<>("Device deleted", HttpStatus.OK);
    }
}
