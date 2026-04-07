package com.example.AdrianoCoffee.Utils;

import com.example.AdrianoCoffee.Dto.MenuDto;
import com.example.AdrianoCoffee.Entity.Menu;
import org.springframework.stereotype.Service;

@Service
public class MenuMappingUtil {
    public MenuDto mapToMenuDto(Menu menu) {
        MenuDto MenuDto = new MenuDto();
        MenuDto.setId(menu.getId());
        MenuDto.setName(menu.getName());
        MenuDto.setPrice(menu.getPrice());
        MenuDto.setDescription(menu.getDescription());
        MenuDto.setVolume(menu.getVolume());
        MenuDto.setImageUrl(menu.getImageUrl());
        MenuDto.setCategory(menu.getCategory());
        return MenuDto;
    }

    public Menu mapToMenu(MenuDto menuDto) {
        Menu Menu = new Menu();
        Menu.setId(menuDto.getId());
        Menu.setName(menuDto.getName());
        Menu.setPrice(menuDto.getPrice());
        Menu.setDescription(menuDto.getDescription());
        Menu.setVolume(menuDto.getVolume());
        Menu.setImageUrl(menuDto.getImageUrl());
        Menu.setCategory(menuDto.getCategory());
        return Menu;
    }
}
