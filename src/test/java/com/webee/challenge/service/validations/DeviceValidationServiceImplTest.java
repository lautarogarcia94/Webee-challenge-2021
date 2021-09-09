package com.webee.challenge.service.validations;

import com.google.firebase.FirebaseException;
import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;
import com.webee.challenge.services.validations.DeviceValidationService;
import com.webee.challenge.services.validations.impl.DeviceValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.bind.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeviceValidationServiceImplTest {

    private DeviceValidationService deviceValidationService;

    @BeforeEach
    public void setUp() {
        deviceValidationService = new DeviceValidationServiceImpl();
    }

    @Test
    void shouldValidateMacOk() {
        assertDoesNotThrow(() -> deviceValidationService.validateMac("FF:FF:FF:FF:FF:FF"));
    }

    @Test
    void shouldThrowValidationExceptionWhenInvalidMac() {
        assertThrows(ValidationException.class, () -> deviceValidationService.validateMac("Invalid MAC Address"));
    }

    @Test
    void shouldValidateIdOk() {
        assertDoesNotThrow(() -> deviceValidationService.validateId("qwertyuiop"));
    }

    @Test
    void shouldThrowValidationExceptionWhenIdHasNot10Characters() {
        String expectedMessage = "Not a valid ID (it does not have 10 characters)";
        try {
            deviceValidationService.validateId("qwer");
            fail();
        } catch (ValidationException validationException) {
            assertEquals(expectedMessage, validationException.getMessage());
        }
    }

    @Test
    void shouldThrowValidationExceptionWhenIdHasOtherCharacterButLetters() {
        String expectedMessage = "Not a valid ID (some character is not a letter between 'a to z')";
        try {
            deviceValidationService.validateId("qwer56789t");
            fail();
        } catch (ValidationException validationException) {
            assertEquals(expectedMessage, validationException.getMessage());
        }
    }

    @Test
    void shouldValidateDeviceOK() {
        DeviceRequest deviceRequest = DeviceRequest.builder()
                .date("10-08-2021")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();

        assertDoesNotThrow(() -> deviceValidationService.validateDeviceRequest(deviceRequest));
    }

    @Test
    void shouldThrowValidationExceptionWhenDateHasInvalidFormat() {
        DeviceRequest deviceRequest = DeviceRequest.builder()
                .date("10082021")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();

        String expectedMessage = "10082021 is not a valid date format (valid format is dd-MM-yyyy)";

        try {
            deviceValidationService.validateDeviceRequest(deviceRequest);
            fail();
        } catch (ValidationException validationException) {
            assertEquals(expectedMessage, validationException.getMessage());
        }
    }

    @Test
    void shouldThrowValidationExceptionWhenDateIsBefore2020() {
        DeviceRequest deviceRequest = DeviceRequest.builder()
                .date("10-08-2019")
                .macAddress("FF:FF:FF:FF:FF:FF")
                .build();

        String expectedMessage = "Not a valid date";

        try {
            deviceValidationService.validateDeviceRequest(deviceRequest);
            fail();
        } catch (ValidationException validationException) {
            assertEquals(expectedMessage, validationException.getMessage());
        }
    }

    @Test
    void shouldThrowValidationExceptionWhenDeviceValidationWithInvalidMac() {
        DeviceRequest deviceRequest = DeviceRequest.builder()
                .date("10-08-2020")
                .macAddress("Invalid MAC Address")
                .build();

        String expectedMessage = "Not a valid MAC Address";

        try {
            deviceValidationService.validateDeviceRequest(deviceRequest);
            fail();
        } catch (ValidationException validationException) {
            assertEquals(expectedMessage, validationException.getMessage());
        }
    }


}
