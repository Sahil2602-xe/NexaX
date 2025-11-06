package com.project.demo.service;

import com.project.demo.model.PaymentDetails;
import com.project.demo.model.User;

public interface PaymentDetailsService {

    public PaymentDetails addPaymentDetails(String accountNumber,
                                            String accountHolderName,
                                            String ifscCode,
                                            String bankName,
                                            User user);

    public PaymentDetails getUserPaymentDetails(User user);
}
