package com.personal.bookmark_backend.config;

import com.personal.bookmark_backend.service.AvatarStorageService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.core.io.Resource;

import java.io.IOException;

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
                .addResourceLocations(location)
                .resourceChain(false)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String path, Resource directory) throws IOException {
                        if (!AvatarStorageService.isAvatarFileName(path)) {
                            return null;
                        }
                        return super.getResource(path, directory);
                    }
                });
    }
}
