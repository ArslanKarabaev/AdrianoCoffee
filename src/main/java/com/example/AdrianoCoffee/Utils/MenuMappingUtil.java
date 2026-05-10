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
        MenuDto.setNameEn(menu.getNameEn());
        MenuDto.setNameKg(menu.getNameKg());
        MenuDto.setDescriptionEn(menu.getDescriptionEn());
        MenuDto.setDescriptionKg(menu.getDescriptionKg());
        return MenuDto;
    }
}
