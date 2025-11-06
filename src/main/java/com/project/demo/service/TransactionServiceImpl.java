package com.project.demo.service;

import com.project.demo.model.Wallet;
import com.project.demo.model.WalletTransaction;
import com.project.demo.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Override
    public List<WalletTransaction> getTransactionByWallet(Wallet wallet) {
        return walletTransactionRepository.findByWallet(wallet);
    }

    @Override
    public WalletTransaction saveTransaction(WalletTransaction transaction) {
        return walletTransactionRepository.save(transaction);
    }
}
