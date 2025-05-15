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

@HttpExchange(RestConstants.VERSION_ONE + RestPaths.User.BASE)
public interface UserOperations {


    @PostExchange(RestPaths.User.REGISTER)
    User register(@RequestBody RegisterRequest registerRequest);

    @GetExchange(RestPaths.User.IDENTITY)
    User getUserByIdentity(@PathVariable Long identity);

    @PutExchange(RestPaths.User.IDENTITY + RestPaths.User.EMAIL_SETTINGS)
    User updateEmailSettings(@PathVariable Long identity, @RequestBody EmailSettingRequest request);

    @PutExchange(RestPaths.User.IDENTITY + RestPaths.User.EMAIL_ADDRESS)
    User updateEmailAddress(@PathVariable Long identity, @RequestBody EmailAddressRequest request);

    @PutExchange(RestPaths.User.IDENTITY + RestPaths.User.RESET_PASSWORD)
    void requestPasswordRecoveryToken(@PathVariable Long identity);

    @GetExchange(RestPaths.User.RESET_PASSWORD + RestPaths.User.VALIDATE_RECOVERY_TOKEN)
    boolean checkPasswordRecoveryToken(@RequestParam String token);

    @PutExchange(RestPaths.User.RESET_PASSWORD)
    void changePasswordFromRecoveryToken(@RequestBody UserRestorePasswordRequest request);

    @PostExchange(RestPaths.User.IDENTITY + RestPaths.Admin.EDIT_PASSWORD)
    void changeUserPassword(@PathVariable Long identity, @RequestBody UserEditPasswordRequest request);
}
