package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Enum.Category;
import com.example.AdrianoCoffee.Enum.Role;
import com.example.AdrianoCoffee.Repository.MenuRepo;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Utils.UsersMappingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {
    public final UsersRepo usersRepo;
    public final MenuRepo menuRepo;
    public final UsersMappingUtil usersMappingUtil;
    private static final String UPLOAD_DIR = "uploads/images/menu/";

    @Autowired
    public AdminService(UsersRepo usersRepo, MenuRepo menuRepo, UsersMappingUtil usersMappingUtil) {
        this.usersRepo = usersRepo;
        this.menuRepo = menuRepo;
        this.usersMappingUtil = usersMappingUtil;
    }

    public List<Users> getAllUsers() {
        return usersRepo.findAll();
    }

    public List<UsersDto> getAllUsersDto() {
        return getAllUsers().stream().map(usersMappingUtil::mapToUsersDto).collect(Collectors.toList());
    }

    public Optional<Users> getUsersById(Long userId) {
        boolean exists = usersRepo.existsById(userId);
        if (!exists) {
            throw new IllegalStateException("There is no User with id " + userId);
        }
        return usersRepo.findById(userId);
    }

    public UsersDto getUserDtoById(Long id) {
        return usersMappingUtil.mapToUsersDto(getUsersById(id).orElse(new Users()));
    }

    public Optional<Users> getUserByName(String firstName, String secondName) {
        Users user = usersRepo.findUsersByFirstNameAndSecondName(firstName, secondName)
                .orElseThrow(() -> new IllegalStateException("There is no User with Name " + firstName + " " + secondName));

        return usersRepo.findUsersByFirstNameAndSecondName(firstName,secondName);
    }

    public UsersDto getUserByNameDto(String firstName, String secondName){
        return usersMappingUtil.mapToUsersDto(getUserByName(firstName,secondName).orElse(new Users()));
    }

    public List<UsersDto> getUsersByFirstName(String firstName) {
        List<Users> users = usersRepo.findUsersByFirstName(firstName);
        return users.stream()
                .map(usersMappingUtil::mapToUsersDto)
                .collect(Collectors.toList());
    }

    public List<UsersDto> getUsersBySecondName(String secondName) {
        List<Users> users = usersRepo.findUsersBySecondName(secondName);
        return users.stream()
                .map(usersMappingUtil::mapToUsersDto)
                .collect(Collectors.toList());
    }


    public Role getUsersRoleById(Long userId) {
        boolean exists = usersRepo.existsById(userId);
        if (!exists) {
            throw new IllegalStateException("There is no User with id " + userId);
        }
        return usersRepo.findById(userId).get().getRole();
    }

    @Transactional
    public void deleteUser(Long userId) {
        Users user = usersRepo.findById(userId).orElseThrow(() -> new IllegalStateException(
                "User with ID " + userId + " doesn`t exists"));
        user.setStatus(false);
        usersRepo.save(user);
    }

    @Transactional
    public void restoreUser(Long userId) {
        Users user = usersRepo.findById(userId).orElseThrow(() -> new IllegalStateException(
                "User with ID " + userId + " doesn`t exists"));
        user.setStatus(true);
        usersRepo.save(user);
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
