package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.commons.rest.dto.Paginated;
import com.portalasig.ms.uaa.constant.RestPaths;
import com.portalasig.ms.uaa.dto.EmailAddressRequest;
import com.portalasig.ms.uaa.dto.EmailSettingRequest;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserRestorePasswordRequest;
import com.portalasig.ms.uaa.service.AuthenticationService;
import com.portalasig.ms.uaa.service.FindUserUseCase;
import com.portalasig.ms.uaa.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(RestConstants.VERSION_ONE + RestPaths.User.USER)
@RequiredArgsConstructor
@Api(value = "User Management System", tags = "User Management")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final FindUserUseCase findUserUseCase;

    @ApiOperation(value = "Register a new user", response = User.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User registered successfully"),
            @ApiResponse(code = 400, message = "Invalid user details")})
    @PostMapping(RestPaths.User.REGISTER)
    public User register(
            @ApiParam(value = "Details for registering a new user", required = true)
            @RequestBody RegisterRequest registerRequest) {
        return userService.registerUser(registerRequest);
    }

    @ApiOperation(value = "Get user by identity", response = User.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User retrieved successfully"),
            @ApiResponse(code = 404, message = "User not found")})
    @GetMapping(RestPaths.User.IDENTITY)
    public User getUserByIdentity(
            @ApiParam(value = "Identity of the user to be fetched", required = true)
            @PathVariable Long identity) {
        return userService.getUserByIdentity(identity);
    }

    @ApiOperation(value = "Find user by query", response = User.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User found successfully"),
            @ApiResponse(code = 404, message = "User not found")})
    @GetMapping(RestPaths.User.FIND)
    public List<User> findUsers(
            @ApiParam(value = "Query. Partial identity, email or full name", required = true)
            @RequestParam String query) {
        return findUserUseCase.findUsers(query);
    }

    @ApiOperation(value = "Retrieve all users with optional role-based filtering and pagination",
            response = Paginated.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Users fetched successfully")})
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    public Paginated<User> findAllUsers(
            @ApiParam(value = "Filter by students only")
            @RequestParam(value = "students_only", required = false, defaultValue = "false") boolean studentsOnly,
            @ApiParam(
                    value = "Filter by professor only")
            @RequestParam(value = "professors_only", required = false, defaultValue = "false") boolean professorsOnly,
            @ApiParam(
                    value = "Pagination information", required = true) Pageable pageable) {
        return userService.findAll(studentsOnly, professorsOnly, pageable);
    }

    @ApiOperation(value = "Update user email settings", response = User.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "User email settings updated successfully"),
            @ApiResponse(code = 400, message = "Invalid email settings")})
    @PutMapping(RestPaths.User.IDENTITY + RestPaths.User.EMAIL_SETTINGS)
    public User updateEmailSettings(
            @PathVariable Long identity,
            @RequestBody EmailSettingRequest request
    ) {
        return userService.updateEmailSettings(identity, request);
    }

    @ApiOperation(value = "Update user email address", response = User.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "User email address updated successfully"),
            @ApiResponse(code = 400, message = "Invalid email address")})
    @PutMapping(RestPaths.User.IDENTITY + RestPaths.User.EMAIL_ADDRESS)
    public User updateEmailAddress(
            @PathVariable Long identity,
            @RequestBody EmailAddressRequest request
    ) {
        return userService.updateEmailAddress(identity, request);
    }

    @ApiOperation(value = "Request password recovery token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Recovery token processed")})
    @PutMapping(RestPaths.User.IDENTITY + RestPaths.User.RESET_PASSWORD)
    public void requestPasswordRecoveryToken(@PathVariable Long identity) {
        userService.requestPasswordRecoveryToken(identity);
    }

    @ApiOperation(value = "Check password recovery token validity")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Token is valid"),
            @ApiResponse(code = 400, message = "Token is invalid")
    })
    @GetMapping(RestPaths.User.RESET_PASSWORD + RestPaths.User.VALIDATE_RECOVERY_TOKEN)
    public boolean checkPasswordRecoveryToken(@RequestParam String token) {
        return authenticationService.isPasswordRecoveryTokenValid(token);
    }

    @ApiOperation(value = "Reset user password with recovery token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password has been changed successfully"),
            @ApiResponse(code = 400, message = "Password change failed"),
    })
    @PutMapping(RestPaths.User.RESET_PASSWORD)
    public void changePassword(@Valid @RequestBody UserRestorePasswordRequest request) {
        userService.resetUserPassword(request);
    }
}
