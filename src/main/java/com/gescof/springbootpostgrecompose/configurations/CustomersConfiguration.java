package com.gescof.springbootpostgrecompose.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CustomersConfiguration {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
