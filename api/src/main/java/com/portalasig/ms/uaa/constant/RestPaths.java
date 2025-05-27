package com.portalasig.ms.uaa.constant;

/**
 * Defines the REST endpoint path constants for the UAA service.
 * These are grouped by concern: Authentication, User, Admin, and FrontEnd routes.
 */
public class RestPaths {

    /**
     * Paths related to authentication operations such as login and token refresh.
     */
    public static class Authentication {
        /** Base path for authentication operations. */
        public static final String AUTHENTICATION = "/auth";

        /** Endpoint for user login. */
        public static final String LOGIN = "/login";

        /** Endpoint to refresh the access token. */
        public static final String REFRESH_TOKEN = "/refresh-token";
    }

    /**
     * Paths related to user management operations.
     */
    public static class User {
        public static final String BASE = "/user";
        public static final String REGISTER = "/register";
        public static final String IDENTITY = "/{identity:\\d+}";
        public static final String EMAIL_SETTINGS = "/email-settings";
        public static final String EMAIL_ADDRESS = "/email";
        public static final String RESET_PASSWORD = "/reset-password";
        public static final String VALIDATE_RECOVERY_TOKEN = "/validate-recovery-token";
        public static final String FIND = "/find";
        public static final String BULK = "/bulk";
    }

    /**
     * Admin-only operations like importing users or editing passwords.
     */
    public static class Admin {
        public static final String IMPORT_USERS = "/_import";
        public static final String EDIT_PASSWORD = "/edit-password";
    }

    /**
     * Routes intended to be used from frontend UI flows.
     */
    public static class FrontEnd {
        public static final String RESET_PASSWORD = "/reiniciar-contrasena";
    }
}
