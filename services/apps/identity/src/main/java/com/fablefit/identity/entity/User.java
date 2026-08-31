package com.fablefit.identity.entity;

import com.fablefit.identity.enums.Role;
import com.fablefit.identity.utils.PublicIdPrefix;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@PublicIdPrefix("usr")
@Table(name="users")
public class User extends BaseEntity{
    @Column(name="username")
    private String userName;

    @Column(name="password")
    private String password;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @JoinColumn(name="tenant_id",nullable=false)
    @ManyToOne(fetch=FetchType.LAZY)
    private Tenant tenant;

    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private Role role;
}
