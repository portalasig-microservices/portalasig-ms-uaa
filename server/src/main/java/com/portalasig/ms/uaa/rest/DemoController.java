package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.uaa.client.UserAuthenticationClient;
import com.portalasig.ms.uaa.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DemoController.BASE_PATH)
@RequiredArgsConstructor
public class DemoController {

    public static final String BASE_PATH = RestConstants.VERSION_ONE + "/demo";

    @Qualifier("userAuthenticationClientV1")
    private final UserAuthenticationClient userAuthenticationClient;

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

    @GetMapping("/test-client/{identity:\\d+}")
    public User testClient(@PathVariable Long identity) {
        return userAuthenticationClient.findUserByIdentity(identity).block();
    }
}
