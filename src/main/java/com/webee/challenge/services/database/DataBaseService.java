package com.webee.challenge.services.database;

import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;

import java.util.List;

public interface DataBaseService {

    void saveDevice(DeviceRequest deviceRequest);

    void deleteDevice(String id);

    Device searchDeviceById(String id);

    Device searchDeviceByMac(String macAddress);

    List<Device> searchAllDevices();
}
