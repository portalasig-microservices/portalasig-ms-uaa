package com.portalasig.ms.uaa.server.rest;

import com.portalasig.ms.uaa.server.service.UserService;
import com.portalasig.ms.uaa.server.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
public class DemoController {

    private final UserService userService;

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

    @GetMapping
    public ResponseEntity<UserEntity> helloBasic() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userService.getUser(authentication.getName());
        return ResponseEntity.ok(user);
    }
}
