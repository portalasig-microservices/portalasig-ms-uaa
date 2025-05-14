package com.portalasig.ms.uaa.client;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.commons.rest.dto.Paginated;
import com.portalasig.ms.uaa.constant.RestPaths;
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

@HttpExchange(RestConstants.VERSION_ONE + RestPaths.User.BASE)
public interface AdminUserOperations {

    @DeleteExchange(RestPaths.User.IDENTITY)
    void deleteUser(@PathVariable Long identity);

    @PostExchange(RestPaths.Admin.IMPORT_USERS)
    void createUsersFromCsv(@RequestParam MultipartFile file) throws IOException;

    @PostExchange
    User upsertUser(@RequestBody UserRequest userRequest);

    @GetExchange(RestPaths.User.FIND)
    List<User> findUsers(@RequestParam String query);

    @GetExchange
    Paginated<User> findAllUsers(
            @RequestParam(value = "students_only", required = false, defaultValue = "false") boolean studentsOnly,
            @RequestParam(value = "professors_only", required = false, defaultValue = "false") boolean professorsOnly,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String sort
    );
}
