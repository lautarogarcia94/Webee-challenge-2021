package com.webee.challenge.services.marshaller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webee.challenge.constants.Constants;
import com.webee.challenge.controllers.DeviceMonitoringController;
import com.webee.challenge.model.Device;
import com.webee.challenge.model.DeviceRequest;
import com.webee.challenge.services.marshaller.MarshallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarshallerServiceImpl implements MarshallerService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceMonitoringController.class);

    @Autowired
    @Qualifier("jsonMarshaller")
    private ObjectMapper mapper;


    @Override
    public String marshallDeviceRequest(DeviceRequest device) {
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(device);
        } catch (JsonProcessingException jsonProcessExc) {
            LOG.error("Something went wrong wile marshalling");
        }
        return (Constants.NULL.equalsIgnoreCase(jsonString)) ? null : jsonString;
    }

    @Override
    public String marshallDevice(Device device) {
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(device);
        } catch (JsonProcessingException jsonProcessExc) {
            LOG.error("Something went wrong wile marshalling");
        }
        return (Constants.NULL.equalsIgnoreCase(jsonString)) ? null : jsonString;
    }

    @Override
    public String marshallDeviceList(List<Device> deviceList) {
        if (deviceList.size() == 1) {
            return marshallDevice(deviceList.get(0));
        }

        StringBuilder marshalledList = new StringBuilder("{");
        deviceList.forEach(device -> {
            String marshalledDevice = marshallDevice(device);
            marshalledList.append(marshalledDevice);
            marshalledList.append(",");
        });

        marshalledList.setLength(marshalledList.length() - 1);
        marshalledList.append("}");
        return marshalledList.toString();
    }
}
