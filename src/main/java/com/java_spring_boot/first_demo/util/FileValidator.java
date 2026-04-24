package com.java_spring_boot.first_demo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class FileValidator {

    private static final Tika TIKA = new Tika();

    // Max sizes
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    // Allowed MIME types
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList("video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo");
    private static final List<String> ALLOWED_DOC_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/zip",
            "application/x-rar-compressed",
            "text/plain"
    );

    public static String detectMimeType(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return TIKA.detect(is);
        }
    }

    public static void validate(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String mimeType = detectMimeType(file);
        long size = file.getSize();

        log.info("Validating file: Name={}, Type={}, Size={}", file.getOriginalFilename(), mimeType, size);

        if (isImage(mimeType)) {
            if (size > MAX_IMAGE_SIZE) throw new IllegalArgumentException("Image size exceeds limit (10MB)");
            if (!ALLOWED_IMAGE_TYPES.contains(mimeType)) throw new IllegalArgumentException("Unsupported image type: " + mimeType);
        } else if (isVideo(mimeType)) {
            if (size > MAX_VIDEO_SIZE) throw new IllegalArgumentException("Video size exceeds limit (100MB)");
            if (!ALLOWED_VIDEO_TYPES.contains(mimeType)) throw new IllegalArgumentException("Unsupported video type: " + mimeType);
        } else {
            if (size > MAX_FILE_SIZE) throw new IllegalArgumentException("File size exceeds limit (50MB)");
            if (!ALLOWED_DOC_TYPES.contains(mimeType)) throw new IllegalArgumentException("Unsupported file type: " + mimeType);
        }
    }

    public static boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public static boolean isVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith("video/");
    }
}
