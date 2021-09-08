package com.webee.challenge.controllers;

import com.google.firebase.database.DatabaseException;
import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;
import com.webee.challenge.services.database.DataBaseService;
import com.webee.challenge.services.validations.DeviceValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.List;

@RestController
@RequestMapping("device-monitoring")
public class DeviceMonitoringController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceMonitoringController.class);

    private DeviceValidationService deviceValidationService;
    private DataBaseService dataBaseService;

    @Autowired
    public DeviceMonitoringController(DeviceValidationService deviceValidationService,
                                      DataBaseService dataBaseService) {
        this.deviceValidationService = deviceValidationService;
        this.dataBaseService = dataBaseService;
    }

    @GetMapping(path = "/get-devices-list", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Device>> getDevices() {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-devices-list");

        List<Device> deviceList = dataBaseService.searchAllDevices();

        return new ResponseEntity<>(deviceList, HttpStatus.OK);
    }

    @GetMapping(path = "/get-device-by-mac/{deviceMac}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getDeviceByMac(@PathVariable String deviceMac) {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-device/{}", deviceMac);

        try {
            deviceValidationService.validateMac(deviceMac);
        } catch (ValidationException validationException) {
            LOG.error("Invalid MAC address: ", validationException);
            return new ResponseEntity<>(validationException.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Device device = dataBaseService.searchDeviceByMac(deviceMac);
        return new ResponseEntity<>(device.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "/get-device-by-id/{deviceID}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getDeviceById(@PathVariable Integer deviceID) {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-device/{}", deviceID);

        try {
            deviceValidationService.validateId(deviceID);
        } catch (ValidationException validationException) {
            LOG.error("Invalid ID: ", validationException);
            return new ResponseEntity<>(validationException.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Device device = dataBaseService.searchDeviceById(deviceID);
        return new ResponseEntity<>(device.toString(), HttpStatus.OK);
    }

    @PostMapping(path = "/register-device", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> registerDevice(@RequestBody DeviceRequest device) {
        LOG.info("Post request received, device: {}", device);

        try {
            deviceValidationService.validateDeviceRequest(device);
            dataBaseService.saveDevice(device);

        } catch (ValidationException validationException) {
            LOG.error("Invalid device: ", validationException);
            return new ResponseEntity<>(validationException.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (DatabaseException databaseException) {
            LOG.error("Device not inserted: ", databaseException);
            return new ResponseEntity<>(databaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Device created", HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/delete-device/{deviceID}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> deleteDeviceByID(@PathVariable Integer deviceID) {
        LOG.info("Delete request received, ID: {}", deviceID);

        try {
            deviceValidationService.validateId(deviceID);
        } catch (ValidationException validationException) {
            return new ResponseEntity<>(validationException.getMessage(), HttpStatus.BAD_REQUEST);
        }

        dataBaseService.deleteDevice(deviceID);
        return new ResponseEntity<>("Device deleted", HttpStatus.OK);
    }
}