package com.webee.challenge.config;

import com.webee.challenge.services.validations.DeviceValidationService;
import com.webee.challenge.services.validations.impl.DeviceValidationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configurations {

    @Bean
    public DeviceValidationService deviceValidationService() {
        return new DeviceValidationServiceImpl();
    }
}
