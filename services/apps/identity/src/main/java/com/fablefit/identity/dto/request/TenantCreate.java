package com.fablefit.identity.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantCreate {
    @NotEmpty
    private String key;

    @NotEmpty
    private String name;

    @NotEmpty
    private String adminUserName;

    @NotEmpty
    private String adminPassword;

    @NotEmpty
    private String adminFirstName;

    @NotEmpty
    private String adminLastName;
}
