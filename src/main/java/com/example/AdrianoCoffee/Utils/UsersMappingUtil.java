package com.example.AdrianoCoffee.Utils;

import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Entity.Users;
import org.springframework.stereotype.Service;

@Service
public class UsersMappingUtil {
    public UsersDto mapToUsersDto(Users users){
        UsersDto usersDto = new UsersDto();
        usersDto.setUser_id(users.getUser_id());
        usersDto.setFirstName(users.getFirstName());
        usersDto.setSecondName(users.getSecondName());
        usersDto.setDateOfBirth(users.getDateOfBirth());
        usersDto.setEmail(users.getEmail());
        usersDto.setMobNum(users.getMobNum());
        usersDto.setPassword(users.getPassword());
        usersDto.setAge(users.getAge());
        return usersDto;
    }

    public Users mapToUsers(UsersDto usersDto){
        Users users = new Users();
        users.setUser_id(usersDto.getUser_id());
        users.setFirstName(usersDto.getFirstName());
        users.setSecondName(usersDto.getSecondName());
        users.setDateOfBirth(usersDto.getDateOfBirth());
        users.setEmail(usersDto.getEmail());
        users.setMobNum(usersDto.getMobNum());
        users.setPassword(usersDto.getPassword());
        users.setAge(usersDto.getAge());
        return users;
    }
}
