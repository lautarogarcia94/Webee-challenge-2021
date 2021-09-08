package com.webee.challenge.services.marshaller;

import com.webee.challenge.model.Device;

import java.util.List;

public interface MarshallerService {

    public String marshallDevice(Device device);

    public String marshallDeviceList(List<Device> deviceList);
}
