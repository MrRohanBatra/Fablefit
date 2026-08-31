package com.fablefit.identity.seeder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "fablefit.seeder")
public class PlatformOwnerSeederProperties {
    private boolean enabled = true;
    private String tenantKey = "fablefit";
    private String tenantName = "FableFit";
    private String ownerEmail = "admin@fablefit.com";
    private String ownerPassword = "Password@123";
}
