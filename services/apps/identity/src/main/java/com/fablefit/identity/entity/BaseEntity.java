package com.fablefit.identity.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fablefit.identity.utils.PublicIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @Column(name="public_id")
    private String publicId;

    @Column(name="created_at")
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(name="updated_at")
    private LocalDateTime updatedAt=LocalDateTime.now();
    
    @PrePersist
    public void setup(){
        publicId=PublicIdGenerator.generate(this.getClass());
    }
}
