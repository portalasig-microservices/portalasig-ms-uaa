package com.portalasig.ms.uaa.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.portalasig.ms.commons.persistence.CodeToEnumMapper;
import com.portalasig.ms.commons.persistence.Codeable;

import java.util.List;
import java.util.Map;

public enum EmailSetting implements Codeable<String> {
    NEWS("NEWS"),
    MEDIA("MEDIA"),
    FORUM("FORUM"),
    COMMENT("COMMENT"),
    ASSIGNMENT("ASSIGNMENT"),
    EVALUATION("EVALUATION"),
    EVENTS("EVENTS"),
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

    public static final Map<Integer, EmailSetting> INDEX_TO_ENUM_MAPPER = Map.of(
            1, NEWS,
            2, MEDIA,
            3, FORUM,
            4, COMMENT,
            5, ASSIGNMENT,
            6, EVALUATION,
            7, EVENTS
    );

    public static String defaultEmailSettings() {
        return String.join(",", List.of(
                EmailSetting.NEWS.getCode(),
                EmailSetting.MEDIA.getCode(),
                EmailSetting.FORUM.getCode(),
                EmailSetting.COMMENT.getCode(),
                EmailSetting.ASSIGNMENT.getCode(),
                EmailSetting.EVALUATION.getCode(),
                EmailSetting.EVENTS.getCode()
        ));
    }

    public static EmailSetting fromIndex(int index) {
        return INDEX_TO_ENUM_MAPPER.getOrDefault(index, INVALID);
    }
}

