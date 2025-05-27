package com.portalasig.ms.uaa.rest;

import com.portalasig.ms.commons.rest.dto.Paginated;
import com.portalasig.ms.commons.rest.exception.BadRequestException;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserRequest;
import com.portalasig.ms.uaa.operation.AdminUserOperations;
import com.portalasig.ms.uaa.service.AdminUserService;
import com.portalasig.ms.uaa.service.FindUserUseCase;
import io.swagger.annotations.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for administrative user operations, implementing {@link AdminUserOperations}.
 * <p>
 * Provides endpoints to manage users: creation, deletion, update, bulk import, and search.
 */
@RestController
@RequiredArgsConstructor
@Api(value = "Admin User Management Controller", tags = "Admin User Management")
@Slf4j
public class AdminUserController implements AdminUserOperations {

    private final AdminUserService adminUserService;
    private final FindUserUseCase findUserUseCase;

    @Override
    public void deleteUser(Long identity) {
        adminUserService.deleteUser(identity);
    }

    @Override
    public void createUsersFromCsv(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            adminUserService.createUsersFromCsv(file.getInputStream());
        } else {
            throw new BadRequestException("File is empty");
        }
    }

    @Override
    public User upsertUser(@Valid UserRequest userRequest) {
        log.info("Upserting user: {}", userRequest.getIdentity());
        return adminUserService.upsertUser(userRequest);
    }

    @Override
    public List<User> findUsers(String query, List<UserRole> userRoles) {
        return findUserUseCase.findUsers(query, userRoles);
    }

    @Override
    public Paginated<User> findAllUsers(
            boolean studentsOnly,
            boolean professorsOnly,
            Integer page,
            Integer size,
            String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.by(sort)));
        return adminUserService.findAll(studentsOnly, professorsOnly, pageable);
    }

    @Override
    public List<User> getUsers(List<Long> identities) {
        return adminUserService.getUsers(identities);
    }
}
