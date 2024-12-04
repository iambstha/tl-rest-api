package com.iambstha.tl_rest_api.entity.enums.user;

public enum UserStatus {
    INACTIVE,
    ACTIVE,
    DELETED,
    BANNED;

    @Override
    public String toString() {
        return this.name();
    }
}
