package com.iambstha.tl_rest_api.entity.enums.user;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public String toString() {
        return this.name();
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
