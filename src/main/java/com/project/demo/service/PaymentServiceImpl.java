package com.project.demo.service;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.project.demo.domain.PaymentMethod;
import com.project.demo.domain.PaymentOrderStatus;
import com.project.demo.model.PaymentOrder;
import com.project.demo.model.User;
import com.project.demo.repository.PaymentOrderRepository;
import com.project.demo.response.PaymentResponse;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecretKey;

    // ✅ Smart environment-based frontend URL selector
    private String getBaseUrl() {
        // First priority — FRONTEND_URL environment variable
        String frontendUrl = System.getenv("FRONTEND_URL");
        if (frontendUrl != null && !frontendUrl.isBlank()) {
            return frontendUrl;
        }

        // Fallback to ENVIRONMENT variable
        String env = System.getenv("ENVIRONMENT");
        if (env != null && env.equalsIgnoreCase("PROD")) {
            return "https://nexa-x-frontend.vercel.app";
        }

        // Default to local dev
        return "http://localhost:5173";
    }

    @Override
    public PaymentOrder createOrder(User user, BigDecimal amount, PaymentMethod paymentMethod) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setAmount(amount);
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        return paymentOrderRepository.findById(id)
                .orElseThrow(() -> new Exception("Payment Order Not Found"));
    }

    @Override
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException {
        if (paymentOrder.getStatus() == null) {
            paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        }

        if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
            if (paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)) {
                RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecretKey);
                Payment payment = razorpay.payments.fetch(paymentId);

                String status = payment.get("status");

                if (status.equals("captured")) {
                    paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    paymentOrderRepository.save(paymentOrder);
                    return true;
                }

                paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepository.save(paymentOrder);
                return false;
            }

            paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
            paymentOrderRepository.save(paymentOrder);
            return true;
        }

        return false;
    }

    // ✅ Razorpay payment link creation
    @Override
    public PaymentResponse createRazorpayPaymentLink(User user, BigDecimal amount, Long orderId)
            throws RazorpayException {

        Long amountInPaise = amount.multiply(BigDecimal.valueOf(100)).longValue(); // Convert INR to paise
        String baseUrl = getBaseUrl();

        try {
            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecretKey);

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amountInPaise);
            paymentLinkRequest.put("currency", "INR");

            JSONObject customer = new JSONObject();
            customer.put("name", user.getFullName());
            customer.put("email", user.getEmail());
            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);

            paymentLinkRequest.put("reminder_enable", true);

            // ✅ Use environment-based callback
            paymentLinkRequest.put("callback_url", baseUrl + "/wallet?order_id=" + orderId);
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

            String paymentLinkUrl = payment.get("short_url");

            PaymentResponse res = new PaymentResponse();
            res.setPayment_url(paymentLinkUrl);
            return res;

        } catch (RazorpayException e) {
            System.err.println("⚠️ Error creating Razorpay Payment Link: " + e.getMessage());
            throw new RazorpayException(e.getMessage());
        }
    }

    // ✅ Stripe payment link creation
    @Override
    public PaymentResponse createStripePaymentLink(User user, BigDecimal amount, Long orderId)
            throws StripeException {

        Stripe.apiKey = stripeSecretKey;
        String baseUrl = getBaseUrl();

        Long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue(); // Convert USD to cents

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl + "/wallet?order_id=" + orderId + "&session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/payment/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Top Up Wallet")
                                                                .build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);

        PaymentResponse response = new PaymentResponse();
        response.setPayment_url(session.getUrl());
        return response;
    }
}
