package com.fablefit.identity.service;

import java.util.List;
import java.util.UUID;

import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.dto.response.TenantCreated;
import com.fablefit.identity.dto.response.TenantResponse;

public interface TenantService {
    TenantCreated createTenant(TenantCreate tenantCreate);

    UUID getTenantIdFromKey(String key);

    List<TenantResponse> getTenants();
}
