package com.wa;

import com.wa.config.DatajudProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DatajudProperties.class)
public class WaApplication {
    public static void main(String[] args) {
        SpringApplication.run(WaApplication.class, args);
    }
}

