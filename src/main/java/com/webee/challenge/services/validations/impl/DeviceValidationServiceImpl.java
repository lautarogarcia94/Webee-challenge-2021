package com.webee.challenge.services.validations.impl;

import com.webee.challenge.constants.Constants;
import com.webee.challenge.model.DeviceRequest;
import com.webee.challenge.services.validations.DeviceValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class DeviceValidationServiceImpl implements DeviceValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceValidationServiceImpl.class);

    /**
     * It validates the MAC Address in order to match the valid format FF:FF:FF:FF:FF:FF
     *
     * @param macAddress device MAC Address to be validated
     * @throws ValidationException exception thrown when the parameter is not a valid MAC Address
     */
    @Override
    public void validateMac(String macAddress) throws ValidationException {
        LOG.info("Validating MAC Address: {}", macAddress);
        if (!macAddress.matches(Constants.REGEXPMAC)) {
            LOG.error("{} is not a valid MAC Address", macAddress);
            throw new ValidationException("Not a valid MAC Address");
        }

        LOG.info("{} is a valid MAC Address", macAddress);
    }

    /**
     * It validates the id in order to match the valid format. The id has 10 characters, and each one
     * has to be a lower case letter.
     *
     * @param id device ID to be validated
     * @throws ValidationException exception thrown when the parameter is not a valid ID
     */
    @Override
    public void validateId(String id) throws ValidationException {
        LOG.info("Validating ID: {}", id);

        if (id.length() != 10) {
            LOG.error("{} is not a valid ID (it does not have 10 characters)", id);
            throw new ValidationException("Not a valid ID (it does not have 10 characters)");
        }

        if (!id.matches(Constants.REGEXID)) {
            LOG.error("{} is not a valid ID (some character is not a letter between 'a to z')", id);
            throw new ValidationException("Not a valid ID (some character is not a letter between 'a to z')");
        }

        LOG.info("{} is a valid ID", id);
    }

    /**
     * It validates the device to be registered. The MAC Address of the device has to match the
     * FF:FF:FF:FF:FF:FF format, and the date has to have ddMMyyyy format and can not be before 01012020
     *
     * @param deviceRequest device to be validated
     * @throws ValidationException Exception thrown when the parameter is not a valid device
     */
    @Override
    public void validateDeviceRequest(DeviceRequest deviceRequest) throws ValidationException {
        String macAddress = deviceRequest.getMacAddress();
        String date = deviceRequest.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        LOG.info("Validating device");

        try {
            LocalDate realDate = LocalDate.parse(date, formatter);
            validateMac(macAddress);
            validateDate(realDate);
        } catch (DateTimeParseException parseException) {
            LOG.error("{} is not a valid date format", date);
            throw new ValidationException(date + " is not a valid date format (valid format is ddMMyyyy)", parseException);
        }

        LOG.info("Device validated");
    }

    /**
     * It validates if a date is after 01012020
     *
     * @param date date to be validated
     * @throws ValidationException Exception thrown when the date is before 01012020
     */
    private void validateDate(LocalDate date) throws ValidationException {
        LOG.info("Validating date: {}", date);
        if (date.compareTo(LocalDate.of(2020, 01, 01)) < 0) {
            LOG.error("{} is not a valid date", date);
            throw new ValidationException("Not a valid date");
        }
        LOG.info("{} is a valid date", date);
    }
}
