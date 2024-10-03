package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Service.ManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500", maxAge = 3600)
@RequestMapping(path = ("api/v2/AdrianoCoffee/Management/"))
public class ManagementController {
    private final ManagementService managementService;

    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @GetMapping(path = "getAllUsers")
    public ResponseEntity<List<UsersDto>> getAllUsers(){return ResponseEntity.ok(managementService.getAllUsersDto());}

    @GetMapping(path = "getUserById/{userId}")
    public ResponseEntity<UsersDto> getUserById(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(managementService.getUserDtoById(userId));
    }
}
