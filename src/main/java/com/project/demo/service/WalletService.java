package com.project.demo.service;

import com.project.demo.model.Order;
import com.project.demo.model.User;
import com.project.demo.model.Wallet;

import java.math.BigDecimal;

public interface WalletService {

    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet, BigDecimal money);
    Wallet findWalletById(Long id) throws Exception;
    Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, BigDecimal amount) throws Exception;
    Wallet payOrderPayment(Order order, User user) throws Exception;
}
