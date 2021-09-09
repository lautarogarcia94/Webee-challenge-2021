package com.webee.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class used as a model for devices stored in FireBase
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private String date;
    private String macAddress;
    private String ID;
}
