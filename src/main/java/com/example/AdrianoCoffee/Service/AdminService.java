package com.example.AdrianoCoffee.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Enum.Category;
import com.example.AdrianoCoffee.Enum.Role;
import com.example.AdrianoCoffee.Repository.MenuRepo;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Utils.UsersMappingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    public final UsersRepo usersRepo;
    public final MenuRepo menuRepo;
    public final UsersMappingUtil usersMappingUtil;
    //private static final String UPLOAD_DIR = "uploads/images/menu/";
    public final Cloudinary cloudinary;

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

        return usersRepo.findUsersByFirstNameAndSecondName(firstName, secondName);
    }

    public UsersDto getUserByNameDto(String firstName, String secondName) {
        return usersMappingUtil.mapToUsersDto(getUserByName(firstName, secondName).orElse(new Users()));
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
