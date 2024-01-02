package com.api.localportfogram.user.service;


import com.api.localportfogram.auth.enums.AuthEnums;
import com.api.localportfogram.auth.utils.JwtTokenProvider;
import com.api.localportfogram.auth.utils.SecurityUtil;
import com.api.localportfogram.exception.dto.BadRequestException;
import com.api.localportfogram.exception.dto.ExceptionEnum;
import com.api.localportfogram.user.dto.Profile;
import com.api.localportfogram.user.dto.User;
import com.api.localportfogram.user.entity.UserEntity;
import com.api.localportfogram.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
   private final JwtTokenProvider jwtTokenProvider;


    @Transactional(readOnly = true)
    public Profile searchProfileById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "유저를 찾을 수 없습니다."));

        return Profile.builder()
                .nickname(user.getNickname())
                .followers(user.getFollowers().stream().count())
                .following(user.getFollowings().stream().count())
                .build();
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities.stream()
                .map(User::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserEntity getMyUserWithAuthorities() {
        UserEntity userEntity = SecurityUtil.getCurrentUsername()
                .flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND));

        return userEntity;
    }

    @Transactional
    public void saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "이미 가입되어 있는 이메일입니다.");
        }

        if (userRepository.existsByNickname(user.getNickname())) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID, "중복된 닉네임입니다.");
        }

        UserEntity userEntity = UserEntity.builder()
                .nickname(user.getNickname())
                .name(user.getName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(AuthEnums.ROLE.ROLE_USER)
                .build();

        userRepository.save(userEntity);
    }

    public Long getUserIdFromToken(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        String name = authentication.getName();
        Optional<UserEntity> user = userRepository.findByEmail(name);
        return user.get().getId();
    }

    @Transactional
    public void deleteMember(Long memberId) {
        UserEntity userEntity = userRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"유저를 찾을 수 없습니다."));

        userRepository.delete(userEntity);
    }

    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"유저를 찾을 수 없습니다."));
    }
}
