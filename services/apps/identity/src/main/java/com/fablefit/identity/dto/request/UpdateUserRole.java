package com.fablefit.identity.dto.request;

import com.fablefit.identity.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRole {
    @NotNull(message = "role cannot be empty")
    private Role role;
}
