package com.project.demo.request;

import com.project.demo.domain.OrderType;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private String coinId;
    private  double quantity;
    private OrderType orderType;
}
