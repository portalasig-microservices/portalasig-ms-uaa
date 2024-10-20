package com.portalasig.ms.uaa.constant;

public class RestPaths {

    public static class Authentication {
        public static final String AUTHENTICATION = "/auth";
        public static final String LOGIN = "/login";
        public static final String REFRESH_TOKEN = "/refresh-token";
    }

    public static class User {
        public static final String USER = "/user";
    public static final String REGISTER = "/register";
    public static final String IDENTITY = "{identity:\\d+}";
    }

    public static class Admin {
        public static final String IMPORT_USERS = "/_import";
    }
}
