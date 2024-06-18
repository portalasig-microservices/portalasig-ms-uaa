package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.uaa.client.UserAuthenticationClient;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.mapper.UserMapper;
import com.portalasig.ms.uaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/demo")
@RequiredArgsConstructor
public class DemoController {

    private final UserService userService;

    private final UserMapper userMapper;

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

    @GetMapping
    public User helloBasic() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserEntity user = userService.getUser(authentication.getName());
        return userMapper.toDto(userService.getUser("fuhranku"));
    }

    @GetMapping("/test-client")
    public User testClient() {
        return userAuthenticationClient.findUserByUsername().block();
    }
}
