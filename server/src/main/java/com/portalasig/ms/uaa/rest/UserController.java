package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.uaa.dto.EmailAddressRequest;
import com.portalasig.ms.uaa.dto.EmailSettingRequest;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserEditPasswordRequest;
import com.portalasig.ms.uaa.dto.UserRestorePasswordRequest;
import com.portalasig.ms.uaa.operation.UserOperations;
import com.portalasig.ms.uaa.service.AuthenticationService;
import com.portalasig.ms.uaa.service.UserService;
import io.swagger.annotations.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that handles user-related operations such as registration, password management, and email settings.
 * <p>
 * This controller implements {@link UserOperations} and delegates the actual logic to {@link UserService} and
 * {@link AuthenticationService}.
 * </p>
 * <p>
 * Routes exposed by this controller include:
 * <ul>
 *     <li>User registration</li>
 *     <li>Password reset and update</li>
 *     <li>Email settings update</li>
 *     <liFetching user profile by identity</li>
 * </ul>
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Api(value = "User Management System", tags = "User Management")
@Slf4j
public class UserController implements UserOperations {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Override
    public User register(RegisterRequest registerRequest) {
        return userService.registerUser(registerRequest);
    }

    @Override
    public List<User> bulkRegister(@RequestBody List<RegisterRequest> request) {
        return userService.bulkRegister(request);
    }

    @Override
    public User getUserByIdentity(Long identity) {
        return userService.getUserByIdentity(identity);
    }

    @Override
    public User updateEmailSettings(Long identity, EmailSettingRequest request) {
        return userService.updateEmailSettings(identity, request);
    }

    @Override
    public User updateEmailAddress(Long identity, EmailAddressRequest request) {
        return userService.updateEmailAddress(identity, request);
    }

    @Override
    public void requestPasswordRecoveryToken(Long identity) {
        userService.requestPasswordRecoveryToken(identity);
    }

    @Override
    public boolean checkPasswordRecoveryToken(String token) {
        return authenticationService.isPasswordRecoveryTokenValid(token);
    }

    @Override
    public void changePasswordFromRecoveryToken(UserRestorePasswordRequest request) {
        userService.resetUserPassword(request);
    }

    @Override
    public void changeUserPassword(Long identity, @Valid UserEditPasswordRequest request) {
        log.info("Editing password of user_id={}", identity);
        userService.changeUserPassword(identity, request);
    }
}
