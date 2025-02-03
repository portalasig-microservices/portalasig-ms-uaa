package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.commons.rest.exception.BadRequestException;
import com.portalasig.ms.uaa.constant.RestPaths;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserEditPasswordRequest;
import com.portalasig.ms.uaa.dto.UserRequest;
import com.portalasig.ms.uaa.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(RestConstants.VERSION_ONE + RestPaths.User.USER)
@RequiredArgsConstructor
@Api(value = "Admin User Management Controller", tags = "Admin User Management")
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @ApiOperation(value = "Delete an user by identity number")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "User deleted successfully"),
            @ApiResponse(code = 400, message = "Invalid user identity")})
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(RestPaths.User.IDENTITY)
    public void deleteUser(
            @ApiParam(value = "Identity of the user to be deleted", required = true)
            @PathVariable Long identity) {
        userService.deleteUser(identity);
    }

    @ApiOperation(value = "Bulk create users from CSV file")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Users created successfully"),
            @ApiResponse(code = 400, message = "Invalid CSV file")})
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(RestPaths.Admin.IMPORT_USERS)
    public void createUsersFromCsv(
            @ApiParam(value = "CSV file containing user data", required = true)
            @RequestParam MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            userService.createUsersFromCsv(file.getInputStream());
        } else {
            throw new BadRequestException("File is empty");
        }
    }

    @ApiOperation(value = "Admin upsert user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User upserted successfully"),
                    @ApiResponse(code = 400, message = "Bad request"),
                    @ApiResponse(code = 500, message = "Internal server error")
            }
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping()
    public User upsertUser(
            @Valid @RequestBody UserRequest userRequest) {
        log.info("Upserting user: {}", userRequest.getIdentity());
        return userService.upsertUser(userRequest);
    }

    @ApiOperation(value = "Admin change user password")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User password edited successfully"),
                    @ApiResponse(code = 400, message = "Bad request"),
                    @ApiResponse(code = 500, message = "Internal server error")
            }
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(RestPaths.User.IDENTITY + RestPaths.Admin.EDIT_PASSWORD)
    public void changeUserPassword (
            @PathVariable Long identity,
            @Valid @RequestBody UserEditPasswordRequest request) {
        log.info("Editing password of user_id={}", identity);
        userService.changeUserPassword(identity, request);
    }
}
