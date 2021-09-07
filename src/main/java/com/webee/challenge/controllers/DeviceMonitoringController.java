package com.webee.challenge.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("device-monitoring")
public class DeviceMonitoringController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceMonitoringController.class);

    @GetMapping(path = "/get-devices-list", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getDevices() {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-devices-list");
        return new ResponseEntity<>("Getting devices list", HttpStatus.OK);
    }

    @GetMapping(path = "/get-device/{deviceInformation}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getDevice(@PathVariable String deviceInformation) {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-device/{}", deviceInformation);
        return new ResponseEntity<>("Getting unique device", HttpStatus.OK);
    }

    @PostMapping(path = "/register-device", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> registerDevice(@RequestBody String device) {
        LOG.info("Post request received, device: {}", device);
        return new ResponseEntity<>("device created", HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/delete-device/{deviceID}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> deleteDeviceByID(@PathVariable String deviceID) {
        LOG.info("Delete request received, ID: {}", deviceID);
        return new ResponseEntity<>("Device deleted", HttpStatus.OK);
    }
}
