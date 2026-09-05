package com.fablefit.identity.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public User createUser(TenantCreate tenantCreate, Tenant tenant) {
        return userRepository.save(User.builder().firstName(tenantCreate.getAdminFirstName())
                .lastName(tenantCreate.getAdminLastName()).userName(tenantCreate.getAdminUserName())
                .tenant(tenant)
                .password(passwordEncoder.encode(tenantCreate.getAdminPassword()))
                .role(Role.ADMIN)
                .build());
    }

    @Override
    public UUID resolveUserPublicIdToInternalId(String publicId) {
        User user = userRepository.findByPublicId(publicId).orElseThrow(
            () -> new UsernameNotFoundException("User not found with public id: " + publicId)
        );
        return user.getId();
    }

    @Override
    public Optional<User> loginUserByTenantId(String username, String password, String tenantId) {
        UUID tenantUuid;
        try {
            tenantUuid = UUID.fromString(tenantId);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        return userRepository.findByTenantIdAndUserName(tenantUuid, username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }
}
