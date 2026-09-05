package com.fablefit.identity.dto.request;

import lombok.Data;

@Data 
public class LoginUser {
    private String tenantKey;
    private String userEmail;
    private String userPassword;
}
