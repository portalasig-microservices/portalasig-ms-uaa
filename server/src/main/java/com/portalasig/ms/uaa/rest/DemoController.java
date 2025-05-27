package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.operation.AdminUserOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Demo controller to verify role-based access control and external HTTP client behavior.
 * <p>
 * Contains endpoints for testing user/admin access and for testing Feign/WebClient integrations with the
 * {@link AdminUserOperations} client.
 */
@RestController
@RequestMapping(DemoController.BASE_PATH)
@RequiredArgsConstructor
public class DemoController {

    /**
     * Base path for demo controller routes.
     */
    public static final String BASE_PATH = RestConstants.VERSION_ONE + "/demo";

    @Qualifier("tokenRelayAdminUserClientV1")
    private final AdminUserOperations adminUserOperations;

    /**
     * Endpoint accessible only by users with the USER authority.
     *
     * @return a welcome message for the USER role
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<String> helloUser() {
        return ResponseEntity.ok("Hello, role user");
    }

    /**
     * Endpoint accessible only by users with the ADMIN authority.
     *
     * @return a welcome message for the ADMIN role
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> helloAdmin() {
        return ResponseEntity.ok("Hello, role ADMIN!!");
    }

    /**
     * Calls the AdminUserOperations client to fetch users by query string and specific roles.
     *
     * @param query
     *         the user search query string
     * @return a list of matching users
     */
    @GetMapping("/test-client")
    public List<User> testClient(@RequestParam String query) {
        return adminUserOperations.findUsers(query, List.of(UserRole.STUDENT, UserRole.PROFESSOR));
    }
}
