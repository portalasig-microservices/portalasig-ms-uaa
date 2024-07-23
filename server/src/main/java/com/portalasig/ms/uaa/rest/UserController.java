package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserRequest;
import com.portalasig.ms.uaa.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.portalasig.ms.uaa.constant.UserPaths.IDENTITY;
import static com.portalasig.ms.uaa.constant.UserPaths.REGISTER;
import static com.portalasig.ms.uaa.constant.UserPaths.USER_PATH;

@RestController
@RequestMapping(RestConstants.VERSION_ONE + USER_PATH)
@RequiredArgsConstructor
@Api(value = "User Management System", tags = "User Management")
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "Register a new user", response = User.class)
    @PostMapping(REGISTER)
    public User register(
            @ApiParam(value = "Details for registering a new user", required = true)
            @RequestBody RegisterRequest registerRequest
    ) {
        return userService.registerUser(registerRequest);
    }

    @ApiOperation(value = "Find user by identity", response = User.class)
    @GetMapping(IDENTITY)
    public User findUserByIdentity(
            @ApiParam(value = "Identity of the user to be fetched", required = true) @PathVariable Long identity
    ) {
        return userService.findUserByIdentity(identity);
    }

    @ApiOperation(value = "Update an existing user", response = User.class)
    @PutMapping(IDENTITY)
    public User updateUser(
            @ApiParam(value = "Identity of the user to be updated", required = true) @PathVariable Long identity,
            @ApiParam(value = "Updated details of the user", required = true) @RequestBody UserRequest user
    ) {
        return userService.updateUser(identity, user);
    }
}
