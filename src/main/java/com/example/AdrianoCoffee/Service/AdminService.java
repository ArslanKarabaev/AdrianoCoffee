package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Repository.MenuRepo;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Utils.UsersMappingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    public final UsersRepo usersRepo;
    public final MenuRepo menuRepo;
    public final UsersMappingUtil usersMappingUtil;

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

    @Transactional
    public void deleteUser(Long userId) {
        Users user = usersRepo.findById(userId).orElseThrow(() -> new IllegalStateException(
                "User with ID " + userId + " doesn`t exists"));
        user.setStatus(false);
    }

    public void addNewItemToMenu(Menu menu) {
        Optional<Menu> menuByName = menuRepo.findMenuByName(menu.getName());
        if (menuByName.isPresent()) {
            throw new IllegalStateException("This product already added");
        }
        menuRepo.save(menu);
    }

    public void deleteItemFromMenu(Long menuId) {
        boolean exists = menuRepo.existsById(menuId);
        if (!exists) {
            throw new IllegalStateException("Product with id " + menuId + " doesn`t exists");
        }
        menuRepo.deleteById(menuId);
    }
}
