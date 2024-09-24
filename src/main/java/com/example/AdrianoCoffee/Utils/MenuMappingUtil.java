package com.example.AdrianoCoffee.Utils;

import com.example.AdrianoCoffee.Dto.MenuDto;
import com.example.AdrianoCoffee.Entity.Menu;
import org.springframework.stereotype.Service;

@Service
public class MenuMappingUtil {
    public MenuDto mapToMenuDto(Menu menu) {
        MenuDto MenuDto = new MenuDto();
        MenuDto.setMenu_id(menu.getMenu_id());
        MenuDto.setName(menu.getName());
        MenuDto.setCategory(menu.getCategory());
        MenuDto.setDescription(menu.getDescription());
        MenuDto.setPrice(menu.getPrice());
        return MenuDto;
    }

    public Menu mapToMenu(MenuDto menuDto) {
        Menu Menu = new Menu();
        Menu.setMenu_id(menuDto.getMenu_id());
        Menu.setName(menuDto.getName());
        Menu.setCategory(menuDto.getCategory());
        Menu.setDescription(menuDto.getDescription());
        Menu.setPrice(menuDto.getPrice());
        return Menu;
    }
}
