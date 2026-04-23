package com.example.AdrianoCoffee.Service.Storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "cloudinary")
public class CloudinaryImageStorageService implements ImageStorageService {
    private final Cloudinary cloudinary;

    public CloudinaryImageStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String saveImage(MultipartFile file) throws IOException {
        Map result = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "adrianocoffee/menu"));
        return (String) result.get("secure_url");
    }

    public void deleteImage(String imageUrl) {
        try {
            cloudinary.uploader().destroy(extractPublicId(imageUrl),
                    ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("Ошибка удаления Cloudinary: " + e.getMessage());
        }
    }

    private String extractPublicId(String url) {
        int idx = url.indexOf("/upload/");
        if (idx == -1) return url;
        String after = url.substring(idx + 8);
        if (after.startsWith("v") && after.contains("/"))
            after = after.substring(after.indexOf("/") + 1);
        int dot = after.lastIndexOf(".");
        return dot != -1 ? after.substring(0, dot) : after;
    }
}