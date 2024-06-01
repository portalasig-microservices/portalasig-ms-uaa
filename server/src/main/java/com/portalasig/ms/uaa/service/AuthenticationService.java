package com.portalasig.ms.uaa.service;

import com.portalasig.ms.uaa.domain.constant.TokenType;
import com.portalasig.ms.uaa.domain.entity.RoleEntity;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.dto.AuthenticationRequest;
import com.portalasig.ms.uaa.dto.AuthenticationResponse;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static String APPLICATION_NAME;
    private final UserRepository userRepository;
    private final UserService userService;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${spring.application.name}")
    private void setApplicationName(String applicationName) {
        APPLICATION_NAME = applicationName;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        UserEntity registeredUserEntity = userService.registerUser(request);
        return AuthenticationResponse
                .builder()
                .token(generateToken(registeredUserEntity, TokenType.ACCESS_TOKEN))
                .refreshToken(generateToken(registeredUserEntity, TokenType.REFRESH_TOKEN))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        );
        UserEntity userEntity = userRepository.findByUsername(
                request.getUserName()).orElseThrow(() -> new IllegalArgumentException("Invalid username or password")
        );
        return AuthenticationResponse
                .builder()
                .token(generateToken(userEntity, TokenType.ACCESS_TOKEN))
                .refreshToken(generateToken(userEntity, TokenType.REFRESH_TOKEN))
                .build();
    }

    private String generateToken(UserEntity userEntity, TokenType tokenType) {
        if (TokenType.ACCESS_TOKEN.equals(tokenType)) {
            return jwtService.generateToken(createClaims(userEntity), userEntity);
        }
        return jwtService.generateRefreshToken(createClaims(userEntity), userEntity);
    }

    private static Map<String, Object> createClaims(UserEntity userEntity) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("issuer", APPLICATION_NAME);
        extraClaims.put("first_name", userEntity.getFirstName());
        extraClaims.put("last_name", userEntity.getLastName());
        extraClaims.put("authorities", userEntity.getRoles().stream().map(RoleEntity::getName).toList());
        return extraClaims;
    }
}
