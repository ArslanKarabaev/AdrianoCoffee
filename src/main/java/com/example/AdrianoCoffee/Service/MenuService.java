package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Dto.MenuDto;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Repository.MenuRepo;
import com.example.AdrianoCoffee.Utils.MenuMappingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepo menuRepository;
    private final MenuMappingUtil menuMappingUtil;

    public List<Menu> getAllMenu() {
        return menuRepository.findAll();
    }

    public List<MenuDto> getAllMenuDto() {
        return getAllMenu().stream().map(menuMappingUtil::mapToMenuDto).collect(Collectors.toList());
    }

    public Optional<Menu> getMenuById(Long menuId) {
        boolean exists = menuRepository.existsById(menuId);
        if (!exists) {
            throw new IllegalStateException("There is no product with ID = " + menuId);
        }
        return menuRepository.findMenuByMenu_id(menuId);
    }

    public Optional<MenuDto> getMenuByIdDto(Long menuId) {
        return getMenuById(menuId).map(menuMappingUtil::mapToMenuDto);
    }
}
