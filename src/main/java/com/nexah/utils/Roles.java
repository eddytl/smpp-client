package com.nexah.utils;

public class Roles {

    public static final String ROLE_USER = "USER";
    public static final String ROLE_SUPPORT = "SUPPORT";
    public static final String ROLE_ADMIN = "ADMIN";

    public static final String USER = "hasAuthority('USER')";
    public static final String SUPPORT = "hasAuthority('SUPPORT')";
    public static final String ADMIN = "hasAuthority('ADMIN')";
    public static final String USER_ADMIN = "hasAnyAuthority('USER', 'ADMIN')";
    public static final String USER_SUPPORT_ADMIN = "hasAnyAuthority('USER', 'SUPPORT', 'ADMIN')";
    public static final String SUPPORT_ADMIN = "hasAnyAuthority('SUPPORT', 'ADMIN')";
}
