package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.commons.rest.dto.Paginated;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.portalasig.ms.uaa.constant.UserPaths.IDENTITY;
import static com.portalasig.ms.uaa.constant.UserPaths.USER_PATH;

@RestController
@RequestMapping(RestConstants.VERSION_ONE + USER_PATH)
@RequiredArgsConstructor
@Api(value = "Admin User Management System", tags = "Admin User Management")
public class AdminUserController {

    private final UserService userService;

    @ApiOperation(value = "Retrieve all users paginated", response = Paginated.class)
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Paginated<User> findAllUsers(
            @ApiParam(value = "Pagination information", required = true) Pageable pageable
    ) {
        return userService.findAll(pageable);
    }

    @ApiOperation(value = "Delete a user by identity")
    @DeleteMapping(IDENTITY)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(
            @ApiParam(value = "Identity of the user to be deleted", required = true) @PathVariable Long identity
    ) {
        userService.deleteUser(identity);
    }

    @ApiOperation(value = "Bulk create users from CSV file")
    @PostMapping("/_import")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void createUsersFromCsv(
            @ApiParam(value = "CSV file containing user data", required = true) @RequestParam MultipartFile file
    ) throws IOException {
        userService.createUsersFromCsv(file.getInputStream());
    }
}
