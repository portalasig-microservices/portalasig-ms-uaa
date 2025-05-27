package com.portalasig.ms.uaa.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.portalasig.ms.commons.persistence.CodeToEnumMapper;
import com.portalasig.ms.commons.persistence.Codeable;

/**
 * Represents the role of a user within the PortalAsig system. Each role has an associated string code and business
 * logic for validation.
 */
public enum UserRole implements Codeable<String> {
    ADMIN("ADMIN"),
    STUDENT("STUDENT"),
    PROFESSOR("PROFESSOR"),
    INVALID("");

    private static final CodeToEnumMapper<String, UserRole> CODE_TO_ENUM_MAPPER =
            new CodeToEnumMapper<>(UserRole.class);

    final String code;

    UserRole(String code) {
        this.code = code;
    }

    /**
     * Converts a string code into the corresponding {@link UserRole} enum.
     *
     * @param code
     *         the role string code
     * @return the matching enum, or throws if invalid
     */
    @JsonCreator
    public static UserRole fromCode(String code) {
        return CODE_TO_ENUM_MAPPER.fromCode(code).get();
    }

    /**
     * Returns the string code for serialization.
     *
     * @return the code for this role
     */
    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Determines if this role is allowed to perform user-facing actions. For example, `ADMIN` and `INVALID` are not
     * considered allowed in certain contexts.
     *
     * @return true if allowed, false otherwise
     */
    public boolean isAllowed() {
        return this != INVALID && this != ADMIN;
    }
}
