package com.personal.bookmark_backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AvatarStorageServiceTest {
    @TempDir
    Path directory;

    @Test
    void rejectsFakeImageRegardlessOfDeclaredContentType() throws Exception {
        var service = new AvatarStorageService(directory.toString());
        var upload = new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1, 2, 3});

        assertThrows(IllegalArgumentException.class, () -> service.store(upload));
        try (var files = Files.list(directory)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void detectsFormatFromImageAndStripsTrailingData() throws Exception {
        var service = new AvatarStorageService(directory.toString());
        var source = new ByteArrayOutputStream();
        assertTrue(ImageIO.write(new BufferedImage(16, 12, BufferedImage.TYPE_INT_ARGB), "png", source));
        source.write(new byte[512]);
        var upload = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", source.toByteArray());

        String name = service.store(upload);
        assertTrue(name.endsWith(".png"));
        assertTrue(Files.size(directory.resolve(name)) < source.size());
        assertEquals(16, ImageIO.read(directory.resolve(name).toFile()).getWidth());

        service.deletePrevious("https://example.com/uploads/avatars/" + name);
        assertFalse(Files.exists(directory.resolve(name)));
    }
}
