package com.fablefit.identity.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.enums.Role;
import com.fablefit.identity.repository.UserRepository;
import com.fablefit.identity.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(TenantCreate tenantCreate,Tenant tenant) {
        return userRepository.save(User.builder().firstName(tenantCreate.getAdminFirstName())
                .lastName(tenantCreate.getAdminLastName()).userName(tenantCreate.getAdminUserName())
                .tenant(tenant)
                .password(passwordEncoder.encode(tenantCreate.getAdminPassword()))
                .role(Role.ADMIN)
                .build());
    }
}
