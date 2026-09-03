package com.fablefit.identity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TenantCreate {
    private String key;
    private String name;
    private String adminUserName;
    private String adminPassword;
    private String adminFirstName;
    private String adminLastName;
}
