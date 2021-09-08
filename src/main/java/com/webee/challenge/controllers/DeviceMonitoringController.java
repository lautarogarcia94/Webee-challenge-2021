package com.webee.challenge.controllers;

import com.google.firebase.database.DatabaseException;
import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;
import com.webee.challenge.services.database.DataBaseService;
import com.webee.challenge.services.marshaller.MarshallerService;
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
    private MarshallerService marshallerService;

    @Autowired
    public DeviceMonitoringController(DeviceValidationService deviceValidationService,
                                      DataBaseService dataBaseService,
                                      MarshallerService marshallerService) {
        this.deviceValidationService = deviceValidationService;
        this.dataBaseService = dataBaseService;
        this.marshallerService = marshallerService;
    }

    @GetMapping(path = "/get-devices-list", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getDevices() {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-devices-list");
        List<Device> deviceList = null;

        try {
            deviceList = dataBaseService.searchAllDevices();
        } catch (DatabaseException dataBaseException) {
            LOG.error("Problem while searching in the database: ", dataBaseException);
            return new ResponseEntity<>(dataBaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String marshalledDevice = marshallerService.marshallDeviceList(deviceList);
        LOG.info("Devices found: {}", deviceList);
        return new ResponseEntity<>(marshalledDevice, HttpStatus.OK);
    }

    @GetMapping(path = "/get-device-by-mac/{deviceMac}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getDeviceByMac(@PathVariable String deviceMac) {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-device/{}", deviceMac);
        Device device = null;

        try {
            deviceValidationService.validateMac(deviceMac);
            device = dataBaseService.searchDeviceByMac(deviceMac);

        } catch (ValidationException validationException) {

            LOG.error("Invalid MAC address: ", validationException);
            return new ResponseEntity<>(validationException.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (DatabaseException dataBaseException) {
            LOG.error("Problem while searching in the database: ", dataBaseException);
            return new ResponseEntity<>(dataBaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String marshalledDevice = marshallerService.marshallDevice(device);
        LOG.info("Device with {} MAC Address found: {}", deviceMac, marshalledDevice);
        return new ResponseEntity<>(marshalledDevice, HttpStatus.OK);
    }

    @GetMapping(path = "/get-device-by-id/{deviceID}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getDeviceById(@PathVariable String deviceID) {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-device/{}", deviceID);
        Device device = null;

        try {
            deviceValidationService.validateId(deviceID);
            device = dataBaseService.searchDeviceById(deviceID);

        } catch (ValidationException validationException) {
            LOG.error("Invalid ID: ", validationException);
            return new ResponseEntity<>(validationException.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (DatabaseException dataBaseException) {
            LOG.error("Problem while searching in the database: ", dataBaseException);
            return new ResponseEntity<>(dataBaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String marshalledDevice = marshallerService.marshallDevice(device);
        LOG.info("Device with {} ID found: {}", deviceID, marshalledDevice);
        return new ResponseEntity<>(marshalledDevice, HttpStatus.OK);
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

        } catch (DatabaseException dataBaseException) {
            LOG.error("Device not inserted: ", dataBaseException);
            return new ResponseEntity<>(dataBaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOG.info("Device {} registered", device.toString());
        return new ResponseEntity<>("Device created", HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/delete-device/{deviceID}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> deleteDeviceByID(@PathVariable String deviceID) {
        LOG.info("Delete request received, ID: {}", deviceID);

        try {
            deviceValidationService.validateId(deviceID);
            dataBaseService.deleteDevice(deviceID);

        } catch (ValidationException validationException) {
            LOG.error("Invalid ID: ", validationException);
            return new ResponseEntity<>(validationException.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (DatabaseException dataBaseException) {
            LOG.error("Device not deleted: ", dataBaseException);
            return new ResponseEntity<>(dataBaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Device deleted", HttpStatus.OK);
    }
}
