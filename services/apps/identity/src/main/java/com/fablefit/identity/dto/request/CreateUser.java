package com.fablefit.identity.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUser {
    @Email
    @NotEmpty
    @JsonAlias({"username", "userName", "userEmail", "email"})
    private String userName;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @Size(min = 4, max = 20)
    private String password;
}
