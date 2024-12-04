package com.iambstha.tl_rest_api.constant;

public abstract class AppConstant {

    public static final long TOKEN_EXPIRATION_TIME = 20 * 60 * 1000; // 20 minutes in milliseconds
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 60 * 60 * 1000; // 1 hour in milliseconds

    public static final String USERNAME_PREFIX = "TL";
    public static final String DEFAULT_ERROR = "Tree Leaf default error.";

}

