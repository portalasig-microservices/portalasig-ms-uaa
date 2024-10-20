package com.portalasig.ms.uaa.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.portalasig.ms.commons.persistence.CodeToEnumMapper;
import com.portalasig.ms.commons.persistence.Codeable;

public enum RoleType implements Codeable<String> {

    USER("USER"), ADMIN("ADMIN"), STUDENT("STUDENT"), PROFESSOR("PROFESSOR"), INVALID("");

    private static final CodeToEnumMapper<String, RoleType> CODE_TO_ENUM_MAPPER =
            new CodeToEnumMapper<>(RoleType.class);

    final String code;

    RoleType(String code) {
        this.code = code;
    }

    @JsonCreator
    public static RoleType fromCode(String code) {
        return CODE_TO_ENUM_MAPPER.fromCode(code).get();
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }
}
