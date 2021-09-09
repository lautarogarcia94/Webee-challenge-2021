package com.webee.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is a model for registering a new device into FireBase
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRequest {

    private String date;
    private String macAddress;
}
