package com.fablefit.identity.dto.response;

import lombok.Data;

@Data
public class TenantCreated {
    private String id;
    private String tenantKey;
    private String tenantName;
    private String ownerUserId;
}
