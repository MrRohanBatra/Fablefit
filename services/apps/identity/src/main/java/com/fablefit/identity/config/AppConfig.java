package com.fablefit.identity.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fablefit.identity.dto.response.TenantResponse;
import com.fablefit.identity.dto.response.UserResponse;
import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper mapper=new ModelMapper();
        mapper.typeMap(Tenant.class,TenantResponse.class)
        .addMappings(map->{
            map.map(Tenant::getPublicId, TenantResponse::setId);
            map.map(Tenant::getName, TenantResponse::setName);
            map.map(Tenant::getKey, TenantResponse::setKey);
        });
        mapper.typeMap(User.class, UserResponse.class)
        .addMapping(User::getPublicId, UserResponse::setId)
        .addMapping(User::getFirstName, UserResponse::setFirstName)
        .addMapping(User::getLastName, UserResponse::setLastName)
        .addMapping(User::getUserName, UserResponse::setUsername);
        return mapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
