package com.webee.challenge.services.validations;

import com.webee.challenge.model.DeviceRequest;

import javax.xml.bind.ValidationException;

public interface DeviceValidationService {

    void validateMac(String macAddress) throws ValidationException;

    void validateId(Integer id) throws ValidationException;

    void validateDeviceRequest(DeviceRequest deviceRequest) throws ValidationException;

}
