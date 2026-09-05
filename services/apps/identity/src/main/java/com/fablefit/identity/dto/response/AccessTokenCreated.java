package com.fablefit.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder  
@NoArgsConstructor 
@AllArgsConstructor 
public class AccessTokenCreated {
    private String accessToken;
}
