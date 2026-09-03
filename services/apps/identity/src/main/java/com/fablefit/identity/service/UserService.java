package com.fablefit.identity.service;

import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;


public interface UserService {
    public User createUser(TenantCreate tenantCreate,Tenant tenant);
}
