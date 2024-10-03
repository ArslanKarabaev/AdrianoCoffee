package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Dto.UsersDto;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Enum.Role;
import com.example.AdrianoCoffee.Service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v2/AdrianoCoffee/Admin/")
@Tag(name = "Admin")
@CrossOrigin(origins = "http://127.0.0.1:5500", maxAge = 3600)
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(
            description = "Get All Users endpoint for ADMIN",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @GetMapping(path = "getAllUsers")
    public ResponseEntity<List<UsersDto>> getAllUsers(){return ResponseEntity.ok(adminService.getAllUsersDto());}

    @Operation(
            description = "Get User By ID endpoint for ADMIN",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @GetMapping(path = "getUserById/{userId}")
    public ResponseEntity<UsersDto> getUserById(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(adminService.getUserDtoById(userId));
    }

    @GetMapping(path = "getUserById/{userId}/Role")
    public ResponseEntity<Role> getUserRoleById(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(adminService.getUsersRoleById(userId));
    }

    @Operation(
            description = "Delete User By ID endpoint for ADMIN",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PutMapping(path = "deleteUser/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId){
        adminService.deleteUser(userId);
    }

    @Operation(
            description = "Add New Item To Menu endpoint for ADMIN",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PostMapping(path = "addNewItemToMenu")
    public void addNewCoffee(@RequestBody Menu menu) {
        adminService.addNewItemToMenu(menu);
    }

    @Operation(
            description = "Delete Menu Item endpoint for ADMIN",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @DeleteMapping(path = "deleteItemFromMenu/{menuId}")
    public void deleteCoffee(@PathVariable("menuId") Long meniId) {
        adminService.deleteItemFromMenu(meniId);
    }


}
