package com.iambstha.tl_rest_api.entity.enums.general;

public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    @Override
    public String toString() {
        return this.name();
    }
}
