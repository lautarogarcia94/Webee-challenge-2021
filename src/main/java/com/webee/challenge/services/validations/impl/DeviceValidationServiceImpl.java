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

    @Override
    public void validateMac(String macAddress) throws ValidationException {
        LOG.info("Validating MAC Address: {}", macAddress);
        if (!macAddress.matches(Constants.REGEXPMAC)) {
            LOG.error("{} is not a valid MAC Address", macAddress);
            throw new ValidationException("Not a valid MAC Address");
        }

        LOG.info("{} is a valid MAC Address", macAddress);
    }

    @Override
    public void validateId(Integer id) throws ValidationException {
        LOG.info("Validating ID: {}", id);

        if (id < 0) {
            LOG.error("{} is not a valid ID", id);
            throw new ValidationException("Not a valid ID (it is a negative number)");
        }

        LOG.info("{} is a valid ID", id);
    }

    @Override
    public void validateDeviceRequest(DeviceRequest deviceRequest) throws ValidationException {
        String macAddress = deviceRequest.getMacAddress();
        String date = deviceRequest.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LOG.info("Validating device");

        try {
            LocalDate realDate = LocalDate.parse(date, formatter);
            validateMac(macAddress);
            validateDate(realDate);
        } catch (DateTimeParseException parseException) {
            LOG.error("{} is not a valid date format", date);
            throw new ValidationException(date + " is not a valid date format", parseException);
        }

        LOG.info("Device validated");
    }

    private void validateDate(LocalDate date) throws ValidationException {
        LOG.info("Validating date: {}", date);
        if (date.compareTo(LocalDate.of(2020, 01, 01)) < 0) {
            LOG.error("{} is not a valid date", date);
            throw new ValidationException("Not a valid date");
        }
        LOG.info("{} is a valid date", date);
    }
}
