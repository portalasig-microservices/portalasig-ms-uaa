package com.portalasig.ms.uaa.operation;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.commons.rest.dto.Paginated;
import com.portalasig.ms.uaa.constant.RestPaths;
import com.portalasig.ms.uaa.constant.UserRole;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.io.IOException;
import java.util.List;

/**
 * Administrative operations for managing users in the PortalAsig system. Includes creation, deletion, bulk retrieval,
 * and CSV import.
 */
@HttpExchange(RestConstants.VERSION_ONE + RestPaths.User.BASE)
public interface AdminUserOperations {

    /**
     * Deletes a user by identity.
     *
     * @param identity
     *         the user's identity
     */
    @DeleteExchange(RestPaths.User.IDENTITY)
    void deleteUser(@PathVariable Long identity);

    /**
     * Creates multiple users from a CSV file.
     *
     * @param file
     *         the CSV file containing user data
     * @throws IOException
     *         if the file can't be read
     */
    @PostExchange(RestPaths.Admin.IMPORT_USERS)
    void createUsersFromCsv(@RequestParam MultipartFile file) throws IOException;

    /**
     * Creates or updates a user.
     *
     * @param userRequest
     *         the user data
     * @return the upserted {@link User}
     */
    @PostExchange
    User upsertUser(@RequestBody UserRequest userRequest);

    /**
     * Searches users based on a query string and optional roles.
     *
     * @param query
     *         search text
     * @param userRoles
     *         optional list of roles to filter
     * @return list of matching users
     */
    @GetExchange(RestPaths.User.FIND)
    List<User> findUsers(
            @RequestParam String query,
            @RequestParam(value = "user_roles", required = false) List<UserRole> userRoles
    );

    /**
     * Retrieves a paginated list of users, with optional filtering for students or professors.
     *
     * @param studentsOnly
     *         if true, returns only student users
     * @param professorsOnly
     *         if true, returns only professor users
     * @param page
     *         page index (0-based)
     * @param size
     *         number of results per page
     * @param sort
     *         sort field
     * @return a {@link Paginated} list of users
     */
    @GetExchange
    Paginated<User> findAllUsers(
            @RequestParam(value = "students_only", required = false, defaultValue = "false") boolean studentsOnly,
            @RequestParam(value = "professors_only", required = false, defaultValue = "false") boolean professorsOnly,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer size,
            @RequestParam(defaultValue = "identity") String sort
    );

    /**
     * Retrieves a list of users by their identities.
     *
     * @param identities
     *         list of user identities
     * @return list of matching users
     */
    @GetExchange(RestPaths.User.BULK)
    List<User> getUsers(@RequestBody List<Long> identities);
}
