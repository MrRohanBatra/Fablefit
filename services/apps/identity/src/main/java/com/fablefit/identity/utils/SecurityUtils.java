package com.fablefit.identity.utils;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.enums.Role;
import com.rohan.exceptionhandler.ApplicationException;
import com.rohan.exceptionhandler.CommonErrorCode;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityUtils {

    public static AuthContext getAuthContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ApplicationException(CommonErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthContext userContext)) {
            throw new ApplicationException(CommonErrorCode.UNAUTHORIZED);
        }

        return userContext;
    }

    public static Tenant getTenant() {
        return getAuthContext().getTenant();
    }

    public static User getUser() {
        return getAuthContext().getUser();
    }

    public static UUID getTenantId() {
        return getTenant().getId();
    }

    public static UUID getUserId() {
        return getUser().getId();
    }

    public static Role getRole() {
        return getUser().getRole();
    }
}