package com.personal.bookmark_backend.controller;

import com.personal.bookmark_backend.mapper.UserMapper;
import com.personal.bookmark_backend.dto.request.auth.LinkGoogleRequest;
import com.personal.bookmark_backend.dto.response.user.UserProfileResponse;
import com.personal.bookmark_backend.entity.User;
import com.personal.bookmark_backend.service.UserProfileService;
import com.personal.bookmark_backend.service.AvatarStorageService;
import com.personal.bookmark_backend.service.GoogleAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;
    private final AvatarStorageService avatarStorageService;
    private final GoogleAuthService googleAuthService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(
            Authentication authentication
    ) {
        User user = userProfileService.getProfile(userId(authentication));
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PostMapping("/google")
    public ResponseEntity<UserProfileResponse> linkGoogle(
            @Valid @RequestBody LinkGoogleRequest request,
            Authentication authentication
    ) {
        User user = googleAuthService.link(
                userId(authentication),
                request.idToken(),
                request.password()
        );
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> updateAvatar(
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {
        String fileName = avatarStorageService.store(file);
        String avatarUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/avatars/")
                .path(fileName)
                .toUriString();
        User user = userProfileService.updateAvatar(
                userId(authentication),
                avatarUrl
        );
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    private Long userId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
