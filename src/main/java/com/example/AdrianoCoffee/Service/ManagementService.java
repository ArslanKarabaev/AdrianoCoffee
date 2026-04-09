package com.example.AdrianoCoffee.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Enum.Category;
import com.example.AdrianoCoffee.Repository.MenuRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagementService {
    public final MenuRepo menuRepo;
    public final Cloudinary cloudinary;

    public void addNewItemToMenu(Menu menu) {
        Optional<Menu> menuByName = menuRepo.findMenuByName(menu.getName());
        if (menuByName.isPresent()) {
            throw new IllegalStateException("This product already added");
        }
        menuRepo.save(menu);
    }

    public String saveImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "adrianocoffee/menu",
                        "resource_type", "image"
                )
        );
        return (String) uploadResult.get("secure_url");

//        File directory = new File(UPLOAD_DIR);
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//
//        String originalFilename = file.getOriginalFilename();
//        String extension = "";
//        if (originalFilename != null && originalFilename.contains(".")) {
//            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        }
//        String fileName = UUID.randomUUID().toString() + extension;
//
//        Path filePath = Paths.get(UPLOAD_DIR + fileName);
//
//        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//        return "/images/menu/" + fileName;
    }

    private void deleteImageFromCloudinary(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("Не удалось удалить изображение из Cloudinary: " + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        int uploadIndex = imageUrl.indexOf("/upload/");
        if (uploadIndex == -1) return imageUrl;

        String afterUpload = imageUrl.substring(uploadIndex + 8);

        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
        }

        int dotIndex = afterUpload.lastIndexOf(".");
        if (dotIndex != -1) {
            afterUpload = afterUpload.substring(0, dotIndex);
        }

        return afterUpload;
    }


    public void deleteItemFromMenu(Long menuId) {
        Menu menu = menuRepo.findMenuById(menuId)
                .orElseThrow(() -> new IllegalStateException("Product with id " + menuId + " doesn`t exists"));

        deleteImageFromCloudinary(menu.getImageUrl());

//        try {
//            Path filePath = Paths.get("uploads" + menu.getImageUrl());
//            Files.deleteIfExists(filePath);
//        } catch (IOException e) {
//            throw new RuntimeException("Ошибка при удалении файла: " + menu.getImageUrl(), e);
//        }

        menuRepo.deleteById(menuId);
    }

    public void updateMenu(Long menuId, String name, Category category, String description, Double price, String volume, MultipartFile image) {
        try {
            Menu existingMenu = menuRepo.findById(menuId)
                    .orElseThrow(() -> new IllegalStateException("Menu item not found"));

            existingMenu.setName(name);
            existingMenu.setCategory(category);
            existingMenu.setDescription(description);
            existingMenu.setPrice(price);
            existingMenu.setVolume(volume);

            if (image != null && !image.isEmpty()) {
                deleteImageFromCloudinary(existingMenu.getImageUrl());

                String imageUrl = saveImage(image);
                existingMenu.setImageUrl(imageUrl);
            }

//            if (image != null && !image.isEmpty()) {
//                String imageUrl = saveImage(image);
//                existingMenu.setImageUrl(imageUrl);
//            }

            menuRepo.save(existingMenu);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения: " + e.getMessage(), e);
        }
    }
}
