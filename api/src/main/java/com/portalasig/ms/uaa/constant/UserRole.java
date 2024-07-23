package com.portalasig.ms.uaa.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.portalasig.ms.commons.persistence.CodeToEnumMapper;
import com.portalasig.ms.commons.persistence.Codeable;

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

    @JsonCreator
    public static UserRole fromCode(String code) {
        return CODE_TO_ENUM_MAPPER.fromCode(code).get();
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }
}
