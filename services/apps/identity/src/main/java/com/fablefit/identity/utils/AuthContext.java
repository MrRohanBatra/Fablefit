package com.fablefit.identity.utils;

import java.util.UUID;

import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.enums.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthContext {
    private User user;
    private Tenant tenant;
    // private Role role;
}
