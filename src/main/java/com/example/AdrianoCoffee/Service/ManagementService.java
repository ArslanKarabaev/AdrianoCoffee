package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Utils.UsersMappingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManagementService {
    public final UsersRepo usersRepo;
    public final UsersMappingUtil usersMappingUtil;

    @Autowired
    public ManagementService(UsersRepo usersRepo, UsersMappingUtil usersMappingUtil) {
        this.usersRepo = usersRepo;
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
}
