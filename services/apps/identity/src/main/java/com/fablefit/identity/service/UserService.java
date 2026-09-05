package com.fablefit.identity.service;

import java.util.Optional;
import java.util.UUID;

import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;


public interface UserService {
    public User createUser(TenantCreate tenantCreate,Tenant tenant);
    public UUID resolveUserPublicIdToInternalId(String publicId);
    public Optional<User> loginUserByTenantId(String username,String password,String tenantId);
}
