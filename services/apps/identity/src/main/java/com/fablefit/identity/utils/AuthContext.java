package com.fablefit.identity.utils;

import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthContext {
    private User user;
    private Tenant tenant;
}
