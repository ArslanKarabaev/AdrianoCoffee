package com.example.AdrianoCoffee.Controller;

import com.example.AdrianoCoffee.Dto.OrderCartDto;
import com.example.AdrianoCoffee.Entity.OrderCart;
import com.example.AdrianoCoffee.Service.OrderCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v2/AdrianoCoffee/OrderCart/")
public class OrderCartController {
    private final OrderCartService orderCartService;

    public OrderCartController(OrderCartService orderCartService) {
        this.orderCartService = orderCartService;
    }

    @Operation(
            description = "Get All Orders endpoint for ADMIN,MANAGER,USER",
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
    @GetMapping(path = "getAll")
    public ResponseEntity<List<OrderCartDto>> getAllOrders() {
        return ResponseEntity.ok(orderCartService.getAllOrdersDto());
    }

    @Operation(
            description = "Add New Order endpoint for ADMIN,MANAGER,USER",
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
    @PostMapping(path = "addNewOrder")
    public void addNewOrder(@RequestBody OrderCart order) {
        orderCartService.addNewOrder(order);
    }

    @Operation(
            description = "Delete Order By ID endpoint for ADMIN,MANAGER,USER",
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
    @DeleteMapping(path = "delete/{orderId}")
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        orderCartService.deleteOrder(orderId);
    }

}
