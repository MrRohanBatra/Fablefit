package com.fablefit.identity.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fablefit.identity.dto.request.CreateUser;
import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.dto.request.UpdateUser;
import com.fablefit.identity.dto.request.UpdateUserRole;
import com.fablefit.identity.dto.response.UserResponse;
import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.enums.Role;
import com.fablefit.identity.exception.UserErrorCode;
import com.fablefit.identity.repository.UserRepository;
import com.fablefit.identity.service.UserService;
import com.fablefit.exceptionhandler.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public User createUser(TenantCreate tenantCreate, Tenant tenant, Role role) {
        if (userRepository.existsByTenantAndUserName(tenant, tenantCreate.getAdminUserName())) {
            throw new ApplicationException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        return userRepository.save(
                User.builder()
                        .firstName(tenantCreate.getAdminFirstName())
                        .lastName(tenantCreate.getAdminLastName())
                        .userName(tenantCreate.getAdminUserName())
                        .tenant(tenant)
                        .password(passwordEncoder.encode(tenantCreate.getAdminPassword()))
                        .role(role)
                        .build()
        );
    }

    @Override
    public UUID resolveUserPublicIdToInternalId(String publicId) {
        User user = userRepository.findByPublicId(publicId).orElseThrow(
                () -> new ApplicationException(UserErrorCode.USER_NOT_FOUND)
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

    @Override
    @Transactional
    public UserResponse createUser(CreateUser createUser, Tenant tenant) {
        if (userRepository.existsByTenantAndUserName(tenant, createUser.getUserName())) {
            throw new ApplicationException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        User user = userRepository.save(
                User.builder()
                        .userName(createUser.getUserName())
                        .firstName(createUser.getFirstName())
                        .lastName(createUser.getLastName())
                        .password(passwordEncoder.encode(createUser.getPassword()))
                        .role(Role.READ)
                        .tenant(tenant)
                        .build()
        );

        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(UpdateUserRole updateUserRole, String userPublicId, UUID tenantId) {
        if (updateUserRole.getRole().equals(Role.SUPER_ADMIN)) {
            throw new ApplicationException(UserErrorCode.UNAUTHORIZED_ROLE_ASSIGNMENT);
        }

        User user = userRepository.findByTenantIdAndPublicId(tenantId, userPublicId)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.USER_NOT_FOUND));

        user.setRole(updateUserRole.getRole());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UpdateUser updateUser, String userPublicId, UUID tenantId) {
        User user = userRepository.findByTenantIdAndPublicId(tenantId, userPublicId)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.USER_NOT_FOUND));

        boolean isUpdated = false;
        if (StringUtils.hasText(updateUser.getFirstName())) {
            user.setFirstName(updateUser.getFirstName());
            isUpdated = true;
        }
        if (StringUtils.hasText(updateUser.getLastName())) {
            user.setLastName(updateUser.getLastName());
            isUpdated = true;
        }
        if (isUpdated) {
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }

        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public List<UserResponse> getAllUsers(UUID tenantId) {
        return userRepository.findByTenantId(tenantId).stream().map(m->modelMapper.map(m, UserResponse.class)).toList();
    }

    @Override
    public UserResponse getUser(UUID userUuid, UUID tenantUuid) {
        User user = userRepository.findByIdAndTenantId(userUuid, tenantUuid)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.USER_NOT_FOUND));
        return modelMapper.map(user, UserResponse.class);
    }
}
