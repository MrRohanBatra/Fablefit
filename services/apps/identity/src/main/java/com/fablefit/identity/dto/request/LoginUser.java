package com.fablefit.identity.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    @NotEmpty
    @JsonAlias({"email", "username", "userName"})
    private String userEmail;

    @NotEmpty
    @JsonAlias({"password"})
    private String userPassword;
}
