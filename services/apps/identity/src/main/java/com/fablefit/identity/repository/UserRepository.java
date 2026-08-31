package com.fablefit.identity.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Global queries
    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);

    Optional<User> findByPublicId(String publicId);

    // Tenant-scoped queries
    List<User> findByTenantId(UUID tenantId);

    Page<User> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<User> findByTenantIdAndUserName(UUID tenantId, String userName);

    Optional<User> findByTenantIdAndPublicId(UUID tenantId, String publicId);

    boolean existsByTenantIdAndUserName(UUID tenantId, String userName);

    List<User> findByTenantIdAndRole(UUID tenantId, Role role);

    Optional<User> findByTenantAndUserName(Tenant tenant, String userName);

    boolean existsByTenantAndUserName(Tenant tenant, String userName);
}
