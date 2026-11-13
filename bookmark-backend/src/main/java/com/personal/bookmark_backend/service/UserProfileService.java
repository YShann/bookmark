package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.User;
import com.personal.bookmark_backend.exception.ResourceNotFoundException;
import com.personal.bookmark_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final AvatarStorageService avatarStorage;
    private final PasswordResetRateLimiter rateLimiter;

    @Transactional(readOnly = true)
    public User getProfile(Long userId) {
        return findUser(userId);
    }

    @Transactional
    public User updateAvatar(Long userId, MultipartFile file, Function<String, String> urlForFile) {
        rateLimiter.checkSource("avatar", userId.toString(), 6);
        User user = userRepository.lockById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
        String previousUrl = user.getAvatarUrl();
        String fileName = avatarStorage.store(file);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_COMMITTED) {
                    avatarStorage.deletePrevious(previousUrl);
                } else if (status == STATUS_ROLLED_BACK) {
                    avatarStorage.delete(fileName);
                }
            }
        });
        user.setAvatarUrl(urlForFile.apply(fileName));
        return user;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
    }
}
