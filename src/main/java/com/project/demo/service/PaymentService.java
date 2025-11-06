package com.project.demo.service;

import com.project.demo.domain.PaymentMethod;
import com.project.demo.model.PaymentDetails;
import com.project.demo.model.PaymentOrder;
import com.project.demo.model.User;
import com.project.demo.response.PaymentResponse;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentOrder createOrder (User user, BigDecimal amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException;

    PaymentResponse createRazorpayPaymentLink (User user, BigDecimal amount, Long orderId) throws RazorpayException;

    PaymentResponse createStripePaymentLink (User user, BigDecimal amount, Long orderId) throws StripeException;
}
