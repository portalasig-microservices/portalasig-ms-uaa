package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.commons.rest.dto.Paginated;
import com.portalasig.ms.uaa.constant.RestPaths;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserRequest;
import com.portalasig.ms.uaa.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@RestController
@RequestMapping(RestConstants.VERSION_ONE + RestPaths.User.USER)
@RequiredArgsConstructor
@Api(value = "User Management System", tags = "User Management")
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "Register a new user", response = User.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User registered successfully"),
            @ApiResponse(code = 400, message = "Invalid user details")})
    @PostMapping(RestPaths.User.REGISTER)
    public User register(
            @ApiParam(value = "Details for registering a new user", required = true)
            @RequestBody RegisterRequest registerRequest) {
        return userService.registerUser(registerRequest);
    }

    @ApiOperation(value = "Find user by identity", response = User.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User found successfully"),
            @ApiResponse(code = 404, message = "User not found")})
    @GetMapping(RestPaths.User.IDENTITY)
    public User findUserByIdentity(
            @ApiParam(value = "Identity of the user to be fetched", required = true)
            @PathVariable Long identity) {
        return userService.findUserByIdentity(identity);
    }

    @ApiOperation(value = "Update an existing user", response = User.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User updated successfully"),
            @ApiResponse(code = 400, message = "Invalid user details")})
    @PutMapping(RestPaths.User.IDENTITY)
    public User updateUser(
            @ApiParam(value = "Identity of the user to be updated", required = true)
            @PathVariable Long identity, @ApiParam(value = "Updated details of the user", required = true)
    @RequestBody UserRequest user) {
        return userService.updateUser(identity, user);
    }

    @ApiOperation(value = "Retrieve all users with optional role-based filtering and pagination",
            response = Paginated.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Users fetched successfully")})
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    public Paginated<User> findAllUsers(
            @ApiParam(value = "Filter by students only")
            @RequestParam(value = "students_only", required = false, defaultValue = "0") boolean studentsOnly,
            @ApiParam(
                    value = "Filter by professor only")
            @RequestParam(value = "professors_only", required = false, defaultValue = "0") boolean professorsOnly,
            @ApiParam(
                    value = "Pagination information", required = true) Pageable pageable) {
        return userService.findAll(studentsOnly, professorsOnly, pageable);
    }
}
