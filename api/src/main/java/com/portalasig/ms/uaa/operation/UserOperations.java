package com.portalasig.ms.uaa.operation;

import com.portalasig.ms.commons.constants.RestConstants;
import com.portalasig.ms.uaa.constant.RestPaths;
import com.portalasig.ms.uaa.dto.EmailAddressRequest;
import com.portalasig.ms.uaa.dto.EmailSettingRequest;
import com.portalasig.ms.uaa.dto.RegisterRequest;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.dto.UserEditPasswordRequest;
import com.portalasig.ms.uaa.dto.UserRestorePasswordRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

/**
 * Defines the HTTP client contract for interacting with the User API.
 * Provides methods for user registration, password recovery, and email preference updates.
 */
@HttpExchange(RestConstants.VERSION_ONE + RestPaths.User.BASE)
public interface UserOperations {

    /**
     * Registers a new user.
     *
     * @param registerRequest the registration details
     * @return the created {@link User}
     */
    @PostExchange(RestPaths.User.REGISTER)
    User register(@RequestBody RegisterRequest registerRequest);

    /**
     * Fetches a user by their identity.
     *
     * @param identity the user's identity
     * @return the corresponding {@link User}, or null if not found
     */
    @GetExchange(RestPaths.User.IDENTITY)
    User getUserByIdentity(@PathVariable Long identity);

    /**
     * Updates the user's email notification settings.
     *
     * @param identity the user's identity
     * @param request  the email settings to apply
     * @return the updated {@link User}
     */
    @PutExchange(RestPaths.User.IDENTITY + RestPaths.User.EMAIL_SETTINGS)
    User updateEmailSettings(@PathVariable Long identity, @RequestBody EmailSettingRequest request);

    /**
     * Updates the user's email address.
     *
     * @param identity the user's identity
     * @param request  the new email address
     * @return the updated {@link User}
     */
    @PutExchange(RestPaths.User.IDENTITY + RestPaths.User.EMAIL_ADDRESS)
    User updateEmailAddress(@PathVariable Long identity, @RequestBody EmailAddressRequest request);

    /**
     * Initiates the password recovery process for the user.
     *
     * @param identity the user's identity
     */
    @PutExchange(RestPaths.User.IDENTITY + RestPaths.User.RESET_PASSWORD)
    void requestPasswordRecoveryToken(@PathVariable Long identity);

    /**
     * Validates a password recovery token.
     *
     * @param token the recovery token
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    @GetExchange(RestPaths.User.RESET_PASSWORD + RestPaths.User.VALIDATE_RECOVERY_TOKEN)
    boolean checkPasswordRecoveryToken(@RequestParam String token);

    /**
     * Changes the user's password using a valid recovery token.
     *
     * @param request the recovery token and new password
     */
    @PutExchange(RestPaths.User.RESET_PASSWORD)
    void changePasswordFromRecoveryToken(@RequestBody UserRestorePasswordRequest request);

    /**
     * Changes a user's password (admin-only endpoint).
     *
     * @param identity the target user's identity
     * @param request  the new password details
     */
    @PostExchange(RestPaths.User.IDENTITY + RestPaths.Admin.EDIT_PASSWORD)
    void changeUserPassword(@PathVariable Long identity, @RequestBody UserEditPasswordRequest request);
}
