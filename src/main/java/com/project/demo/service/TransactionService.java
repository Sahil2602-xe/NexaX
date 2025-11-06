package com.project.demo.service;

import com.project.demo.model.Wallet;
import com.project.demo.model.WalletTransaction;

import java.util.List;

public interface TransactionService {

    // Get all transactions of a wallet
    List<WalletTransaction> getTransactionByWallet(Wallet wallet);

    // Save a transaction record
    WalletTransaction saveTransaction(WalletTransaction transaction);
}
