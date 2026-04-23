package com.example.AdrianoCoffee.Service.Storage;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageStorageService {
    String saveImage(MultipartFile file) throws IOException;
    void deleteImage(String imageUrl);
}