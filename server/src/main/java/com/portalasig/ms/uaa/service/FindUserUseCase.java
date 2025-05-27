package com.portalasig.ms.uaa.service;

import com.portalasig.ms.commons.rest.exception.BadRequestException;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.mapper.UserMapper;
import com.portalasig.ms.uaa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Use case for finding users based on search criteria and allowed roles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FindUserUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Value("${ms.uaa.rest.find-user.max-result-size:10}")
    private final Integer maxResultSize;

    /**
     * Searches users based on a free-text query and a list of valid {@link UserRole}s. Only ADMIN or PROFESSOR roles
     * can execute this method.
     *
     * @param query
     *         the input text used to match user identity, email, or name
     * @param userRoles
     *         the roles to filter the user by
     * @return list of matching users
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    public List<User> findUsers(String query, List<UserRole> userRoles) {
        List<UserRole> validRoles = validateAndTransformToEntityRoles(userRoles);
        Pageable pageable = PageRequest.of(0, maxResultSize);
        List<UserEntity> potentialUsers = userRepository.smartSearchUsers(
                query.trim().toLowerCase(),
                validRoles,
                pageable
        );
        return potentialUsers.stream().map(userMapper::toDto).toList();
    }

    /**
     * Validates the user roles and ensures none of them are restricted (e.g. ADMIN, INVALID).
     *
     * @param userRoles
     *         roles to validate
     * @return a list of allowed user roles, defaults to STUDENT and PROFESSOR if input is null or empty
     * @throws BadRequestException
     *         if any role is not allowed
     */
    public List<UserRole> validateAndTransformToEntityRoles(List<UserRole> userRoles) {
        if (userRoles == null || userRoles.isEmpty()) {
            userRoles = List.of(UserRole.STUDENT, UserRole.PROFESSOR);
        }
        for (UserRole userRole : userRoles) {
            if (!userRole.isAllowed()) {
                throw new BadRequestException("Roles filtering denied");
            }
        }
        return userRoles;
    }
}
