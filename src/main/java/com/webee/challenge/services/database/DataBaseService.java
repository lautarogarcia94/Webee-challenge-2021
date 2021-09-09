package com.webee.challenge.services.database;

import com.google.firebase.FirebaseException;
import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;

import java.util.List;

public interface DataBaseService {

    void registerDevice(DeviceRequest deviceRequest) throws FirebaseException;

    void deleteDevice(String id) throws FirebaseException;

    Device searchDeviceById(String id) throws FirebaseException;

    Device searchDeviceByMac(String macAddress) throws FirebaseException;

    List<Device> searchAllDevices() throws FirebaseException;
}
