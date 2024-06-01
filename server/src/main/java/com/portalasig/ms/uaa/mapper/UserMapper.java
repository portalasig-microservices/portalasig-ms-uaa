package com.portalasig.ms.uaa.mapper;

import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.dto.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Converts to dto.
     *
     * @param userEntity
     *         user entity
     * @return User
     */
    User toDto(UserEntity userEntity);

    /**
     * Converts to entity.
     *
     * @param user
     *         user
     * @return UserEntity
     */
    UserEntity toEntity(User user);
}
