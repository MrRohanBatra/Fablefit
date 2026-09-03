package com.fablefit.identity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fablefit.identity.dto.request.TenantCreate;
import com.fablefit.identity.dto.response.TenantCreated;
import com.fablefit.identity.service.TenantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenant/")
public class TenantController {
    private final TenantService tenantService;
    @PostMapping
    public ResponseEntity<TenantCreated> createTenant(TenantCreate tenantCreate){
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.createTenant(tenantCreate));
    }
}
