package com.fablefit.identity.service;

import java.util.List;
import java.util.UUID;

import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.dto.response.TenantResponse;
import com.fablefit.identity.dto.response.TenantCreated;

public interface TenantService {
    public TenantCreated createTenant(TenantCreate tenantCreate);
    public UUID getTenantIdFromKey(String key);
    public List<TenantResponse> getTenants();
}
