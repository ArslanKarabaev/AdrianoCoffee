package com.example.AdrianoCoffee.Utils;

import com.example.AdrianoCoffee.Dto.OrderCartDto;
import com.example.AdrianoCoffee.Entity.OrderCart;
import org.springframework.stereotype.Service;

@Service
public class OrderCartMappingUtil {
    public OrderCartDto mapToOrderCartDto(OrderCart orderCart){
        OrderCartDto orderCartDto = new OrderCartDto();
        orderCartDto.setOrder_id(orderCart.getOrder_id());
        orderCartDto.setItems(orderCart.getItems());
        orderCartDto.setSum(orderCart.getSum());
        return orderCartDto;
    }

    public OrderCart mapToOrderCart(OrderCartDto orderCartDto){
        OrderCart orderCart = new OrderCart();
        orderCart.setOrder_id(orderCartDto.getOrder_id());
        orderCart.setItems(orderCartDto.getItems());
        orderCart.setSum(orderCartDto.getSum());
        return orderCart;
    }
}
