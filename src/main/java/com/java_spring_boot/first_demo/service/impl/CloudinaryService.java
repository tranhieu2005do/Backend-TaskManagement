package com.java_spring_boot.first_demo.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.java_spring_boot.first_demo.dto.response.UploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import com.java_spring_boot.first_demo.util.FileValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public UploadResult upload(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        try {
            byte[] bytest = file.getBytes();
            String mimeType = FileValidator.detectMimeType(file);
            String resourceType = mapMimeToCloudinaryType(mimeType);

            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", resourceType,
                    "use_filename", true,
                    "unique_filename", true);

            // Force download for raw files (PDF, DOCS, etc)
            if ("raw".equals(resourceType)) {
                options.put("flags", "attachment");
            }

            Map uploadResult = cloudinary.uploader().upload(bytest, options);

            String secureUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();
            Long bytes = uploadResult.get("bytes") != null ? ((Number) uploadResult.get("bytes")).longValue()
                    : file.getSize();

            String thumbnailUrl = null;
            Double duration = null;

            if ("video".equals(resourceType)) {
                thumbnailUrl = generateVideoThumbnail(publicId);
                if (uploadResult.get("duration") != null) {
                    duration = ((Number) uploadResult.get("duration")).doubleValue();
                }
            } else if ("image".equals(resourceType)) {
                thumbnailUrl = secureUrl;
            }

            return UploadResult.builder()
                    .url(secureUrl)
                    .publicId(publicId)
                    .resourceType(resourceType)
                    .thumbnailUrl(thumbnailUrl)
                    .duration(duration)
                    .originalFileName(originalFilename)
                    .size(bytes)
                    .mimeType(mimeType)
                    .fileExtension(extension)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String mapMimeToCloudinaryType(String mimeType) {
        if (mimeType == null)
            return "raw";
        if (mimeType.startsWith("image/"))
            return "image";
        if (mimeType.startsWith("video/"))
            return "video";
        return "raw";
    }

    private String generateVideoThumbnail(String publicId) {
        return cloudinary.url()
                .resourceType("video")
                .format("jpg")
                .transformation(
                        new Transformation()
                                .startOffset("1")
                                .width(400)
                                .height(300)
                                .crop("fill"))
                .generate(publicId);
    }

}
