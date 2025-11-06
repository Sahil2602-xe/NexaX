package com.project.demo.controller;

import com.project.demo.domain.OrderType;
import com.project.demo.model.Coin;
import com.project.demo.model.Order;
import com.project.demo.model.User;
import com.project.demo.request.CreateOrderRequest;
import com.project.demo.service.CoinService;
import com.project.demo.service.OrderService;
import com.project.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;

//    @Autowired
//   private WalletTransactionService walletTransactionService;

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateOrderRequest req) throws Exception{
        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findById(req.getCoinId());

        Order order = orderService.processOrder(coin,
                req.getQuantity(),
                req.getOrderType(),
                user);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long orderId) throws Exception{

        User user = userService.findUserProfileByJwt(jwtToken);

        Order order = orderService.getOrderById(orderId);
        if(order.getUser().getId().equals(user.getId())){
            return ResponseEntity.ok(order);
        }else{
            throw  new Exception("Access denied");
        }
    }
    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrdersForUser(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam(required = false) OrderType order_type,
            @RequestParam(required = false) String asset_symbol
    )throws Exception{


        Long userId = userService.findUserProfileByJwt(jwtToken).getId();

        List<Order> userOrders = orderService.getAllOrderOfUser(userId, order_type, asset_symbol);
        return ResponseEntity.ok(userOrders);
    }
}
