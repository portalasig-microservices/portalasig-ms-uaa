package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.uaa.operation.AdminUserOperations;
import com.portalasig.ms.uaa.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(DemoController.BASE_PATH)
@RequiredArgsConstructor
public class DemoController {

    public static final String BASE_PATH = RestConstants.VERSION_ONE + "/demo";

    @Qualifier("tokenRelayAdminUserClientV1")
    private final AdminUserOperations adminUserOperations;

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<String> helloUser() {
        return ResponseEntity.ok("Hello, role user");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> helloAdmin() {
        return ResponseEntity.ok("Hello, role ADMIN!!");
    }

    @GetMapping("/test-client")
    public List<User> testClient(@RequestParam String query) {
        return adminUserOperations.findUsers(query);
    }
}
