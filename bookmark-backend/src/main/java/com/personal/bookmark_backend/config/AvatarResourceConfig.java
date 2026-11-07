package com.personal.bookmark_backend.config;

import com.personal.bookmark_backend.service.AvatarStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AvatarResourceConfig implements WebMvcConfigurer {

    private final AvatarStorageService avatarStorageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = avatarStorageService.getStorageDirectory().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }

        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations(location);
    }
}
