package com.webee.challenge.services.marshaller;

import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;

import java.util.List;

public interface MarshallerService {

    String marshallDevice(Device device);

    String marshallDeviceRequest(DeviceRequest device);

    String marshallDeviceList(List<Device> deviceList);
}
