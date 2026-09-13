package com.fablefit.identity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.dto.response.TenantCreated;
import com.fablefit.identity.service.TenantService;
import com.fablefit.identity.utils.ApiEnvelope;

import lombok.RequiredArgsConstructor;

@RestController
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenant")
public class TenantController {
    private final TenantService tenantService;
    
    @PostMapping({"", "/"})
    public ResponseEntity<TenantCreated> createTenant(@RequestBody TenantCreate tenantCreate){
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.createTenant(tenantCreate));
    }

    @GetMapping({"", "/"})
    public ResponseEntity<ApiEnvelope<?>> listTenants(){
        return ResponseEntity.status(HttpStatus.OK).body(ApiEnvelope.success(tenantService.getTenants()));
    }
}
