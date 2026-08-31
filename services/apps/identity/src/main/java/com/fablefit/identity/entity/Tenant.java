package com.fablefit.identity.entity;

import com.fablefit.identity.utils.PublicIdPrefix;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tenants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@PublicIdPrefix("ten")
public class Tenant extends BaseEntity {
    @Column(name="key")
    private String key;
    @Column(name="name")
    private String name;

}
