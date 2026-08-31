package com.fablefit.identity.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.enums.Role;
import com.fablefit.identity.repository.TenantRepository;
import com.fablefit.identity.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlatformOwnerSeeder implements CommandLineRunner {

    private final PlatformOwnerSeederProperties properties;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!properties.isEnabled()) {
            log.info("Platform Owner Seeder is disabled via configuration.");
            return;
        }

        log.info("Checking and seeding platform owner and default tenant...");

        // 1. Find or create default platform tenant
        Tenant tenant = tenantRepository.findByKey(properties.getTenantKey())
                .orElseGet(() -> {
                    log.info("Platform tenant '{}' not found. Creating tenant...", properties.getTenantKey());
                    Tenant newTenant = Tenant.builder()
                            .key(properties.getTenantKey())
                            .name(properties.getTenantName())
                            .build();
                    return tenantRepository.save(newTenant);
                });

        // 2. Find or create SUPER_ADMIN user
        if (!userRepository.existsByUserName(properties.getOwnerEmail())) {
            log.info("Platform owner user '{}' not found. Creating SUPER_ADMIN...", properties.getOwnerEmail());
            User superAdmin = User.builder()
                    .userName(properties.getOwnerEmail())
                    .password(passwordEncoder.encode(properties.getOwnerPassword()))
                    .firstName("Platform")
                    .lastName("Owner")
                    .role(Role.SUPER_ADMIN)
                    .tenant(tenant)
                    .build();
            userRepository.save(superAdmin);
            log.info("SUPER_ADMIN '{}' successfully created with publicId: {}", 
                    properties.getOwnerEmail(), superAdmin.getPublicId());
        } else {
            log.info("Platform owner user '{}' already exists. Skipping user creation.", properties.getOwnerEmail());
        }

        log.info("Platform Owner Seeding completed.");
    }
}