package com.portalasig.ms.uaa.constant;

public class RestPaths {

    public static class Authentication {
        public static final String AUTHENTICATION = "/auth";
        public static final String LOGIN = "/login";
        public static final String REFRESH_TOKEN = "/refresh-token";
    }

    public static class User {
        public static final String BASE = "/user";
        public static final String REGISTER = "/register";
        public static final String IDENTITY = "/{identity:\\d+}";
        public static final String EMAIL_SETTINGS = "/email-settings";
        public static final String EMAIL_ADDRESS = "/email";
        public static final String RESET_PASSWORD = "/reset-password" ;
        public static final String VALIDATE_RECOVERY_TOKEN = "/validate-recovery-token" ;
        public static final String FIND = "/find" ;
        public static final String BULK = "/bulk";
    }

    public static class Admin {
        public static final String IMPORT_USERS = "/_import";
        public static final String EDIT_PASSWORD = "/edit-password";
    }

    public static class FrontEnd {
        public static final String RESET_PASSWORD = "/reiniciar-contrasena";
    }
}
