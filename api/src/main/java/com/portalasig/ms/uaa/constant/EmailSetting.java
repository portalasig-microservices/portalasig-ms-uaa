package com.portalasig.ms.uaa.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.portalasig.ms.commons.persistence.CodeToEnumMapper;
import com.portalasig.ms.commons.persistence.Codeable;

import java.util.List;
import java.util.Map;

/**
 * Enum representing the different types of email notification settings
 * a user can enable or disable.
 * <p>
 * Each setting maps to a unique code string, which can be serialized
 * and deserialized using {@link JsonValue} and {@link JsonCreator}.
 */
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

    /**
     * Converts a string code into the corresponding {@link EmailSetting} enum value.
     *
     * @param code the string representation of the email setting
     * @return the corresponding {@link EmailSetting} enum
     * @throws java.util.NoSuchElementException if the code does not map to any enum
     */
    @JsonCreator
    public static EmailSetting fromCode(String code) {
        return CODE_TO_ENUM_MAPPER.fromCode(code).get();
    }

    /**
     * Returns the string code for the current enum value.
     *
     * @return the string representation of the setting
     */
    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Maps numeric indexes to enum values, useful for legacy systems or UI bindings.
     */
    public static final Map<Integer, EmailSetting> INDEX_TO_ENUM_MAPPER = Map.of(
            1, NEWS,
            2, MEDIA,
            3, FORUM,
            4, COMMENT,
            5, ASSIGNMENT,
            6, EVALUATION,
            7, EVENTS
    );

    /**
     * Returns a comma-separated string of all the default email settings.
     *
     * @return a CSV string of enabled setting codes
     */
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

    /**
     * Gets an {@link EmailSetting} enum value by numeric index.
     *
     * @param index the numeric index (e.g., 1 for NEWS, 2 for MEDIA, etc.)
     * @return the corresponding enum or {@link EmailSetting#INVALID} if not found
     */
    public static EmailSetting fromIndex(int index) {
        return INDEX_TO_ENUM_MAPPER.getOrDefault(index, INVALID);
    }
}
