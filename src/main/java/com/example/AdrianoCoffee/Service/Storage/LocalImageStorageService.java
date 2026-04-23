package com.example.AdrianoCoffee.Service.Storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
public class LocalImageStorageService implements ImageStorageService {
    private static final String UPLOAD_DIR = "uploads/images/menu/";

    public String saveImage(MultipartFile file) throws IOException {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) directory.mkdirs();

        String fileName = UUID.randomUUID() + getExtension(file);
        Files.copy(file.getInputStream(),
                Paths.get(UPLOAD_DIR + fileName),
                StandardCopyOption.REPLACE_EXISTING);
        return "/images/menu/" + fileName;
    }

    public void deleteImage(String imageUrl) {
        try {
            Files.deleteIfExists(Paths.get("uploads" + imageUrl));
        } catch (IOException e) {
            System.err.println("Ошибка удаления: " + e.getMessage());
        }
    }

    private String getExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        return name != null && name.contains(".")
                ? name.substring(name.lastIndexOf(".")) : "";
    }
}
