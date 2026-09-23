package com.fablefit.identity.service.impl;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.dto.response.TenantCreated;
import com.fablefit.identity.dto.response.TenantResponse;
import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.enums.Role;
import com.fablefit.identity.exception.TenantErrorCode;
import com.fablefit.identity.repository.TenantRepository;
import com.fablefit.identity.service.TenantService;
import com.fablefit.identity.service.UserService;
import com.fablefit.exceptionhandler.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Override
    @Transactional
    public TenantCreated createTenant(TenantCreate tenantCreate) {
        if (tenantRepository.existsByKey(tenantCreate.getKey())) {
            throw new ApplicationException(TenantErrorCode.TENANT_ALREADY_EXISTS);
        }

        Tenant tenant = tenantRepository.save(
                Tenant.builder()
                        .key(tenantCreate.getKey())
                        .name(tenantCreate.getName())
                        .build()
        );

        User user = userService.createUser(tenantCreate, tenant, Role.ADMIN);

        return TenantCreated.builder()
                .id(tenant.getPublicId())
                .tenantKey(tenant.getKey())
                .tenantName(tenant.getName())
                .ownerUserId(user.getPublicId())
                .build();
    }

    @Override
    public UUID getTenantIdFromKey(String key) {
        Tenant tenant = tenantRepository.findByKey(key).orElseThrow(
                () -> new ApplicationException(TenantErrorCode.TENANT_NOT_FOUND)
        );
        return tenant.getId();
    }

    @Override
    public List<TenantResponse> getTenants() {
        List<Tenant> tenants = tenantRepository.findAll();
        return tenants.stream()
                .map(t -> modelMapper.map(t, TenantResponse.class))
                .toList();
    }
}
