package com.example.AdrianoCoffee.Service;

import com.example.AdrianoCoffee.Repository.OrderCartRepo;
import com.example.AdrianoCoffee.Utils.OrderCartMappingUtil;
import com.example.AdrianoCoffee.Entity.OrderCart;
import com.example.AdrianoCoffee.Dto.OrderCartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderCartService {

    private final OrderCartRepo orderCartRepo;
    private final OrderCartMappingUtil orderCartMappingUtil;

    @Autowired
    public OrderCartService(OrderCartRepo orderCartRepo, OrderCartMappingUtil orderCartMappingUtil) {
        this.orderCartRepo = orderCartRepo;
        this.orderCartMappingUtil = orderCartMappingUtil;
    }

    public List<OrderCart> getAllOrders() {
        return orderCartRepo.findAll();
    }
    public List<OrderCartDto> getAllOrdersDto(){return getAllOrders().stream().map(orderCartMappingUtil::mapToOrderCartDto).collect(Collectors.toList());}

    public void addNewOrder(OrderCart orderCart) {
        orderCartRepo.save(orderCart);
    }

    public void deleteOrder(Long orderID) {
        boolean exists = orderCartRepo.existsById(orderID);
        if (!exists) {
            throw new IllegalStateException("Order with id " + orderID + " does not exists");
        }
        orderCartRepo.deleteById(orderID);
    }

}
