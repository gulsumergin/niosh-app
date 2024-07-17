package com.example.rwl.config;

import com.example.rwl.model.CalculationRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public CalculationRequest calculationRequest() {
        return new CalculationRequest();
    }
}
