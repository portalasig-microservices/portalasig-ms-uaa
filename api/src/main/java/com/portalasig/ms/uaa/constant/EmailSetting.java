package com.portalasig.ms.uaa.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.portalasig.ms.commons.persistence.CodeToEnumMapper;
import com.portalasig.ms.commons.persistence.Codeable;

import java.util.List;

public enum EmailSetting implements Codeable<String> {
    NEWS("NEWS"),
    MEDIA("MEDIA"),
    FORUM("FORUM"),
    COMMENT("COMMENT"),
    ASSIGNMENT("ASSIGNMENT"),
    EVALUATION("EVALUATION"),
    INVALID("");

    private static final CodeToEnumMapper<String, EmailSetting> CODE_TO_ENUM_MAPPER =
            new CodeToEnumMapper<>(EmailSetting.class);
    final String code;

    EmailSetting(String code) {
        this.code = code;
    }

    @JsonCreator
    public static EmailSetting fromCode(String code) {
        return CODE_TO_ENUM_MAPPER.fromCode(code).get();
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    public static String defaultEmailSettings() {
        return String.join(",", List.of(
                EmailSetting.EVALUATION.getCode(),
                EmailSetting.ASSIGNMENT.getCode(),
                EmailSetting.COMMENT.getCode(),
                EmailSetting.FORUM.getCode(),
                EmailSetting.MEDIA.getCode(),
                EmailSetting.NEWS.getCode()
        ));
    }
}

