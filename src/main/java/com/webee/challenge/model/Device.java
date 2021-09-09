package com.webee.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is a model for devices stored in Firebase
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
