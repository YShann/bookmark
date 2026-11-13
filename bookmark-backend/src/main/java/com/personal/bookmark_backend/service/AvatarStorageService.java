package com.personal.bookmark_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AvatarStorageService {

    private static final Logger log = LoggerFactory.getLogger(AvatarStorageService.class);
    private static final long MAX_AVATAR_BYTES = 10L * 1024 * 1024;
    private static final long MAX_PIXELS = 16_000_000;
    private static final Pattern AVATAR_NAME = Pattern.compile(
            "[0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12}\\.(?:jpg|png|webp)");

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

        String fileName = null;
        try {
            try (var source = file.getInputStream(); var input = new MemoryCacheImageInputStream(source)) {
                var readers = ImageIO.getImageReaders(input);
                if (!readers.hasNext()) {
                    throw new IllegalArgumentException("請上傳有效的 JPG 或 PNG 圖片");
                }
                var reader = readers.next();
                try {
                    String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                    if (!format.equals("jpeg") && !format.equals("png")) {
                        throw new IllegalArgumentException("請上傳有效的 JPG 或 PNG 圖片");
                    }
                    reader.setInput(input, true, true);
                    if ((long) reader.getWidth(0) * reader.getHeight(0) > MAX_PIXELS) {
                        throw new IllegalArgumentException("頭像不可超過 1600 萬像素");
                    }
                    var image = reader.read(0);
                    Files.createDirectories(storageDirectory);
                    Path output = Files.createTempFile(storageDirectory, "avatar-", ".tmp");
                    try {
                        if (!ImageIO.write(image, format, output.toFile()) || Files.size(output) > MAX_AVATAR_BYTES) {
                            throw new IllegalArgumentException("處理後的圖片不可超過 10 MB");
                        }
                        String name = UUID.randomUUID() + (format.equals("jpeg") ? ".jpg" : ".png");
                        Files.move(output, storageDirectory.resolve(name));
                        fileName = name;
                    } finally {
                        Files.deleteIfExists(output);
                    }
                } finally {
                    reader.dispose();
                }
            }
            return fileName;
        } catch (javax.imageio.IIOException exception) {
            delete(fileName);
            throw new IllegalArgumentException("無法處理此頭像圖片");
        } catch (IOException exception) {
            delete(fileName);
            throw new IllegalStateException("頭像圖片儲存失敗", exception);
        }
    }

    public Path getStorageDirectory() {
        return storageDirectory;
    }

    public static boolean isAvatarFileName(String name) {
        return name != null && AVATAR_NAME.matcher(name).matches();
    }

    void deletePrevious(String url) {
        if (url == null) return;
        String path;
        try {
            path = URI.create(url).getPath();
        } catch (IllegalArgumentException exception) {
            return;
        }
        if (path != null && path.contains("/uploads/avatars/")) {
            delete(path.substring(path.lastIndexOf('/') + 1));
        }
    }

    void delete(String fileName) {
        if (!isAvatarFileName(fileName)) return;
        try {
            Files.deleteIfExists(storageDirectory.resolve(fileName));
        } catch (IOException exception) {
            log.warn("無法清理頭像檔案 {}", fileName, exception);
        }
    }
}
