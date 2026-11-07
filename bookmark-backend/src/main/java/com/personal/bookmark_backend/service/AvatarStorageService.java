package com.personal.bookmark_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class AvatarStorageService {

    private static final long MAX_AVATAR_BYTES = 10L * 1024 * 1024;

    private final Path storageDirectory;

    public AvatarStorageService(
            @Value("${bookmark.avatar.storage-directory:uploads/avatars}") String storageDirectory
    ) {
        this.storageDirectory = Path.of(storageDirectory).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("請選擇要上傳的頭像圖片");
        }
        if (file.getSize() > MAX_AVATAR_BYTES) {
            throw new IllegalArgumentException("圖片大小不可超過 10 MB");
        }

        String extension = extensionFor(file.getContentType());
        String fileName = UUID.randomUUID() + extension;
        Path destination = storageDirectory.resolve(fileName).normalize();
        if (!destination.getParent().equals(storageDirectory)) {
            throw new IllegalArgumentException("無效的圖片檔名");
        }

        try {
            Files.createDirectories(storageDirectory);
            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return fileName;
        } catch (IOException exception) {
            throw new IllegalStateException("頭像圖片儲存失敗", exception);
        }
    }

    public Path getStorageDirectory() {
        return storageDirectory;
    }

    private String extensionFor(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("無法辨識圖片格式");
        }

        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("僅支援 JPG、PNG 或 WebP 圖片");
        };
    }
}
