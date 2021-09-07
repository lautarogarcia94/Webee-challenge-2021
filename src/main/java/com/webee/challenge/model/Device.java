package com.webee.challenge.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Device {

    private LocalDate date;
    private String macAddress;
    private Integer ID;
}
