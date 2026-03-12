package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Dto.CartItemDto;
import com.example.AdrianoCoffee.Entity.Cart;
import com.example.AdrianoCoffee.Entity.Menu;
import com.example.AdrianoCoffee.Entity.Users;
import com.example.AdrianoCoffee.Repository.CartRepo;
import com.example.AdrianoCoffee.Repository.MenuRepo;
import com.example.AdrianoCoffee.Repository.UsersRepo;
import com.example.AdrianoCoffee.Utils.OrderCartMappingUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepo cartRepo;
    private final UsersRepo usersRepo;
    private final MenuRepo menuRepo;
    private final OrderCartMappingUtil mappingUtil;

    public CartService(CartRepo cartRepo, UsersRepo usersRepo, MenuRepo menuRepo, OrderCartMappingUtil mappingUtil) {
        this.cartRepo = cartRepo;
        this.usersRepo = usersRepo;
        this.menuRepo = menuRepo;
        this.mappingUtil = mappingUtil;
    }

    // Добавить товар в корзину
    @Transactional
    public CartItemDto addToCart(Long userId, Long menuItemId, Integer quantity) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Menu menuItem = menuRepo.findById(menuItemId)
                .orElseThrow(() -> new IllegalStateException("Menu item not found"));

        // Проверяем, есть ли уже этот товар в корзине
        Cart cart = cartRepo.findByUserAndMenuItem(user, menuItem)
                .orElse(null);

        if (cart != null) {
            // Увеличиваем количество
            cart.setQuantity(cart.getQuantity() + quantity);
        } else {
            // Создаём новую запись
            cart = Cart.builder()
                    .user(user)
                    .menuItem(menuItem)
                    .quantity(quantity)
                    .build();
        }

        cart = cartRepo.save(cart);
        return mappingUtil.mapToDto(cart);
    }

    // Получить корзину пользователя
    public List<CartItemDto> getCart(Long userId) {
        List<Cart> cartItems = cartRepo.findByUserId(userId);
        return cartItems.stream()
                .map(mappingUtil::mapToDto)
                .collect(Collectors.toList());
    }

    // Обновить количество товара
    @Transactional
    public CartItemDto updateQuantity(Long cartId, Integer quantity) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalStateException("Cart item not found"));

        if (quantity <= 0) {
            cartRepo.delete(cart);
            return null;
        }

        cart.setQuantity(quantity);
        cart = cartRepo.save(cart);
        return mappingUtil.mapToDto(cart);
    }

    // Удалить товар из корзины
    @Transactional
    public void removeFromCart(Long cartId) {
        cartRepo.deleteById(cartId);
    }

    // Очистить всю корзину
    @Transactional
    public void clearCart(Long userId) {
        cartRepo.deleteByUserId(userId);
    }

    // Посчитать общую сумму
    public Double calculateTotal(Long userId) {
        List<Cart> cartItems = cartRepo.findByUserId(userId);
        return cartItems.stream()
                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
    }


}