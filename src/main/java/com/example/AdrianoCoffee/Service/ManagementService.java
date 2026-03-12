package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Enum.Category;
import com.example.AdrianoCoffee.Repository.MenuRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagementService {
    public final MenuRepo menuRepo;
    private static final String UPLOAD_DIR = "uploads/images/menu/";

    public ManagementService(MenuRepo menuRepo) {
        this.menuRepo = menuRepo;
    }

    public void addNewItemToMenu(Menu menu) {
        Optional<Menu> menuByName = menuRepo.findMenuByName(menu.getName());
        if (menuByName.isPresent()) {
            throw new IllegalStateException("This product already added");
        }
        menuRepo.save(menu);
    }

    public String saveImage(MultipartFile file) throws IOException {
        // Создаём директорию, если её нет
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Генерируем уникальное имя файла
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // Путь для сохранения
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        // Сохраняем файл
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Возвращаем URL для доступа к изображению
        return "/images/menu/" + fileName;
    }

    public void deleteItemFromMenu(Long menuId) {
        Menu menu = menuRepo.findMenuById(menuId)
                .orElseThrow(() -> new IllegalStateException("Product with id " + menuId + " doesn`t exists"));

        try {
            Path filePath = Paths.get("uploads" + menu.getImageUrl());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при удалении файла: " + menu.getImageUrl(), e);
        }

        menuRepo.deleteById(menuId);
    }

    public void updateMenu(Long menuId, String name, Category category, String description, Double price, String volume, MultipartFile image) {
        try {
            // Получаем существующее блюдо
            Menu existingMenu = menuRepo.findById(menuId)
                    .orElseThrow(() -> new IllegalStateException("Menu item not found"));

            // Обновляем данные
            existingMenu.setName(name);
            existingMenu.setCategory(category);
            existingMenu.setDescription(description);
            existingMenu.setPrice(price);
            existingMenu.setVolume(volume);

            // Обновляем изображение только если загружено новое
            if (image != null && !image.isEmpty()) {
                String imageUrl = saveImage(image);
                existingMenu.setImageUrl(imageUrl);
            }

            // Сохраняем изменения
            menuRepo.save(existingMenu);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения: " + e.getMessage(), e);
        }
    }
}
