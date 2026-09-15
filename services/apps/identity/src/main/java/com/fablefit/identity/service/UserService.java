package com.fablefit.identity.service;

import java.util.Optional;
import java.util.UUID;

import com.fablefit.identity.dto.request.CreateUser;
import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.dto.response.UserResponse;
import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.enums.Role;


public interface UserService {
    User createUser(TenantCreate tenantCreate, Tenant tenant, Role role);

    UserResponse createUser(CreateUser createUser);

    UUID resolveUserPublicIdToInternalId(String publicId);

    Optional<User> loginUserByTenantId(String username, String password, String tenantId);
}
