package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.uaa.dto.AuthenticationRequest;
import com.portalasig.ms.uaa.dto.AuthenticationResponse;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authenticate")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public AuthenticationResponse register(
            @RequestBody RegisterRequest registerRequest
    ) {
        return authenticationService.register(registerRequest);
    }

    @PostMapping
    public AuthenticationResponse authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return authenticationService.authenticate(request);
    }
}
