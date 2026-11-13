package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.User;
import com.personal.bookmark_backend.exception.ResourceNotFoundException;
import com.personal.bookmark_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getProfile(Long userId) {
        return findUser(userId);
    }

    @Transactional
    public User updateAvatar(Long userId, String avatarImageUrl) {
        User user = userRepository.lockById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
        user.setAvatarUrl(avatarImageUrl == null || avatarImageUrl.isBlank()
                ? null
                : avatarImageUrl.trim());
        return userRepository.save(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
    }
}
