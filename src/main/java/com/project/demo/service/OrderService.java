package com.project.demo.service;

import com.project.demo.domain.OrderType;
import com.project.demo.model.Coin;
import com.project.demo.model.Order;
import com.project.demo.model.OrderItem;
import com.project.demo.model.User;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(long orderId) throws Exception;

    List<Order> getAllOrderOfUser(Long userId, OrderType orderType, String assetSymbol);

    Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;
}
