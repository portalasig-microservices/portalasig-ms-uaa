package com.portalasig.ms.uaa.service;

import com.portalasig.ms.uaa.domain.entity.UserEntity;
import com.portalasig.ms.uaa.dto.User;
import com.portalasig.ms.uaa.mapper.UserMapper;
import com.portalasig.ms.uaa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindUserUseCase {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Value("${ms.uaa.rest.find-user.max-result-size:10}")
    private final Integer maxResultSize;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    public List<User> findUsers(String query) {
        Pageable pageable = PageRequest.of(0, maxResultSize);
        List<UserEntity> potentialUsers = userRepository.smartSearchUsers(query.trim().toLowerCase(), pageable);
        return potentialUsers.stream().map(userMapper::toDto).toList();
    }
}
