package com.fablefit.identity.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fablefit.identity.dto.request.CreateUser;
import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.dto.request.UpdateUser;
import com.fablefit.identity.dto.request.UpdateUserRole;
import com.fablefit.identity.dto.response.UserResponse;
import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.enums.Role;


public interface UserService {
    User createUser(TenantCreate tenantCreate, Tenant tenant, Role role);

    UserResponse createUser(CreateUser createUser, Tenant tenant);

    UUID resolveUserPublicIdToInternalId(String publicId);

    Optional<User> loginUserByTenantId(String username, String password, String tenantId);

    UserResponse updateUserRole(UpdateUserRole updateUserRole, String userPublicId, UUID tenantId);

    UserResponse updateUser(UpdateUser updateUser, String userPublicId, UUID tenantId);

    List<UserResponse> getAllUsers(UUID tenantId);

    UserResponse getUser(UUID userUuid,UUID tenantUuid);
}
