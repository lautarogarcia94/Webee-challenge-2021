package com.webee.challenge.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DeviceRequest {

    private LocalDate date;
    private String macAddress;
}
