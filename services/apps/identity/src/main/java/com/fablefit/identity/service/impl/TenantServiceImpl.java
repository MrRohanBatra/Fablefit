package com.fablefit.identity.service.impl;

import org.springframework.stereotype.Service;

import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.dto.response.TenantCreated;
import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.repository.TenantRepository;
import com.fablefit.identity.service.TenantService;
import com.fablefit.identity.service.UserService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    // private final UserRepository userRepository;
    private final UserService userService;
    @Override
    public TenantCreated createTenant(TenantCreate tenantCreate) {
        // throw new UnsupportedOperationException("Unimplemented method
        // 'createTenant'");
        Tenant tenant = tenantRepository.save(Tenant.builder().key(tenantCreate.getKey()).name(tenantCreate.getName()).build());
        User user=userService.createUser(tenantCreate, tenant);
        TenantCreated tenantCreated=new TenantCreated();
        tenantCreated.setId(tenant.getPublicId());
        tenantCreated.setTenantKey(tenant.getKey());
        tenantCreated.setTenantName(tenant.getName());
        tenantCreated.setOwnerUserId(user.getPublicId());
        return tenantCreated;
    }
}
