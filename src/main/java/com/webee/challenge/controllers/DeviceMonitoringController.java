package com.webee.challenge.controllers;

import com.google.firebase.FirebaseException;
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

    /**
     * Returns a JSON formatted List of saved devices, when receiving a GET request in the
     * endpoint: /device-monitoring/get-devices-list.
     *
     * @return ResponseEntity<String> there are two possible responses when a request reaches this
     *          endpoint:
     *
     *          1) HttpStatus = 200, the body of the response has a marshall list of all registered
     *          devices.
     *
     *          2) HttpStatus = 500, the body of the response has a message with the problem, related
     *          to the database.
     */
    @GetMapping(path = "/get-devices-list", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getDevices() {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-devices-list");
        List<Device> deviceList = null;

        try {
            deviceList = dataBaseService.searchAllDevices();
        } catch (FirebaseException firebaseException) {
            LOG.error("Problem while searching in the database: ", firebaseException);
            return new ResponseEntity<>(firebaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String marshalledDevice = marshallerService.marshallDeviceList(deviceList);
        LOG.info("Devices found: {}", deviceList);
        return new ResponseEntity<>(marshalledDevice, HttpStatus.OK);
    }

    /**
     * Returns a JSON formatted device if exists, when receiving a GET request in the endpoint:
     * /device-monitoring/get-device-by-mac/macAddress.
     *
     * @param deviceMac MAC Address of the device
     *
     * @return ResponseEntity<String> there are three possible responses when a request reaches this
     *          endpoint:
     *
     *          1) HttpStatus = 200, the body of the response has the marshall device that was found.
     *
     *          2) HttpStatus = 400, the body of the response has a message with the problem, related
     *          to a validation of the MAC Address.
     *
     *          3) HttpStatus = 500, the body of the response has a message with the problem, related
     *          to the database.
     */
    @GetMapping(path = "/get-device-by-mac/{deviceMac}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getDeviceByMac(@PathVariable String deviceMac) {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-device-by-mac/{}", deviceMac);
        Device device = null;

        try {
            deviceValidationService.validateMac(deviceMac);
            device = dataBaseService.searchDeviceByMac(deviceMac);

        } catch (ValidationException validationException) {

            LOG.error("Invalid MAC address: ", validationException);
            return new ResponseEntity<>(validationException.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (FirebaseException firebaseException) {
            LOG.error("Problem while searching in the database: ", firebaseException);
            return new ResponseEntity<>(firebaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String marshalledDevice = marshallerService.marshallDevice(device);
        LOG.info("Device with {} MAC Address found: {}", deviceMac, marshalledDevice);
        return new ResponseEntity<>(marshalledDevice, HttpStatus.OK);
    }

    /**
     * Returns a JSON formatted device if exists, when receiving a GET request in the endpoint:
     * /device-monitoring/get-device-by-id/deviceID.
     *
     * @param deviceID ID of the device
     *
     * @return ResponseEntity<String> there are three possible responses when a request reaches this
     *          endpoint:
     *
     *          1) HttpStatus = 200, the body of the response has the marshall device that was found.
     *
     *          2) HttpStatus = 400, the body of the response has a message with the problem, related
     *          to a validation of the ID.
     *
     *          3) HttpStatus = 500, the body of the response has a message with the problem, related
     *          to the database.
     */
    @GetMapping(path = "/get-device-by-id/{deviceID}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getDeviceById(@PathVariable String deviceID) {
        LOG.info("GET Request received, endpoint: /device-monitoring/get-device-by-id/{}", deviceID);
        Device device = null;

        try {
            deviceValidationService.validateId(deviceID);
            device = dataBaseService.searchDeviceById(deviceID);

        } catch (ValidationException validationException) {
            LOG.error("Invalid ID: ", validationException);
            return new ResponseEntity<>(validationException.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (FirebaseException firebaseException) {
            LOG.error("Problem while searching in the database: ", firebaseException);
            return new ResponseEntity<>(firebaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String marshalledDevice = marshallerService.marshallDevice(device);
        LOG.info("Device with {} ID found: {}", deviceID, marshalledDevice);
        return new ResponseEntity<>(marshalledDevice, HttpStatus.OK);
    }

    /**
     * Register a device, when receiving a POST request in the endpoint: /device-monitoring/register-device
     * It expects a body message like:
     * {
     *    "date": "21042020",
     *    "macAddress": "FF:AA:FF:24:24:FF"
     * }
     *
     * @param device Device parameter (date and MAC address only)
     *
     * @return ResponseEntity<String> there are three possible responses when a request reaches this
     *          endpoint:
     *
     *          1) HttpStatus = 200, the body of the response has a message "device registered".
     *
     *          2) HttpStatus = 400, the body of the response has a message with the problem, related
     *          to a validation of the device (either the date or the MAC address).
     *
     *          3) HttpStatus = 500, the body of the response has a message with the problem, related
     *          to the database.
     */
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

        } catch (FirebaseException firebaseException) {
            LOG.error("Device not inserted: ", firebaseException);
            return new ResponseEntity<>(firebaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOG.info("Device registered: {}", marshallerService.marshallDeviceRequest(device));
        return new ResponseEntity<>("Device registered", HttpStatus.CREATED);
    }


    /**
     * Delete a device based on its ID, if exists.
     *
     * @param deviceID ID of the device
     *
     * @return ResponseEntity<String> there are three possible responses when a request reaches this
     *          endpoint:
     *
     *          1) HttpStatus = 200, the body of the response has a message "device deleted".
     *
     *          2) HttpStatus = 400, the body of the response has a message with the problem, related
     *          to a validation of the device ID.
     *
     *          3) HttpStatus = 500, the body of the response has a message with the problem, related
     *          to the database.
     */
    @DeleteMapping(path = "/delete-device/{deviceID}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> deleteDeviceByID(@PathVariable String deviceID) {
        LOG.info("Delete request received, ID: {}", deviceID);

        try {
            deviceValidationService.validateId(deviceID);
            dataBaseService.deleteDevice(deviceID);

        } catch (ValidationException validationException) {
            LOG.error("Invalid ID: ", validationException);
            return new ResponseEntity<>(validationException.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (FirebaseException firebaseException) {
            LOG.error("Device not deleted: ", firebaseException);
            return new ResponseEntity<>(firebaseException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Device deleted", HttpStatus.OK);
    }
}
