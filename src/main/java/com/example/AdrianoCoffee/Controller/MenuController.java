package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Dto.MenuDto;
import com.example.AdrianoCoffee.Service.MenuService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v2/AdrianoCoffee/Menu/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MenuController {
    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(
            description = "Get All Menu endpoint for ADMIN,MANAGER,USER",
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
    @GetMapping(path = "getAllMenu")
    public ResponseEntity<List<MenuDto>> getAllMenu(){return ResponseEntity.ok(menuService.getAllMenuDto());}

    @Operation(
            description = "Get Menu By ID endpoint for ADMIN,MANAGER,USER",
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
    @GetMapping(path = "getMenuById/{menuId}")
    public ResponseEntity<Optional<MenuDto>> getMenuById(@PathVariable("menuId") Long menuId){
        return ResponseEntity.ok(menuService.getMenuByIdDto(menuId));}


}
