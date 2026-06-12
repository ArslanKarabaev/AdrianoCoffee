package com.example.AdrianoCoffee.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Enum.Category;
import com.example.AdrianoCoffee.Repository.MenuRepo;
import com.example.AdrianoCoffee.Service.Payment.AsyncOrderService;
import com.example.AdrianoCoffee.Service.Storage.ImageStorageService;
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
    private final ImageStorageService imageStorageService;
    private final TranslationService translationService;
    private final AsyncOrderService asyncOrderService;

    public void addNewItemToMenu(Menu menu) {
        Optional<Menu> menuByName = menuRepo.findMenuByName(menu.getName());
        if (menuByName.isPresent()) {
            throw new IllegalStateException("This product already added");
        }
        menuRepo.save(menu);
        asyncOrderService.translateMenuAsync(menu);
    }

    public String saveImage(MultipartFile file) throws IOException {
        return imageStorageService.saveImage(file);
    }

    public void deleteImageFromCloudinary(String imageUrl) {
        imageStorageService.deleteImage(imageUrl);
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

    public void updateMenu(Long menuId, String name, Category category, String description,
                           Double price, String volume, MultipartFile image,
                           String nameEn, String nameKg,
                           String descriptionEn, String descriptionKg) {
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
                existingMenu.setImageUrl(saveImage(image));
            }

            // Если переводы введены вручную — используем их
            // Если нет — переводим автоматически
            boolean hasManualTranslation = (nameEn != null && !nameEn.isBlank())
                    || (nameKg != null && !nameKg.isBlank());

            if (hasManualTranslation) {
                if (nameEn != null && !nameEn.isBlank()) existingMenu.setNameEn(nameEn);
                if (nameKg != null && !nameKg.isBlank()) existingMenu.setNameKg(nameKg);
                if (descriptionEn != null && !descriptionEn.isBlank()) existingMenu.setDescriptionEn(descriptionEn);
                if (descriptionKg != null && !descriptionKg.isBlank()) existingMenu.setDescriptionKg(descriptionKg);
                menuRepo.save(existingMenu);
            } else {
                menuRepo.save(existingMenu);
                asyncOrderService.translateMenuAsync(existingMenu); // автоперевод в фоне
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения: " + e.getMessage(), e);
        }
    }
}
