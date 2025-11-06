package com.project.demo.service;

import com.project.demo.domain.WalletTransactionType;
import com.project.demo.model.Wallet;
import com.project.demo.model.WalletTransaction;
import com.project.demo.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TransactionRecorderService {

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    public void record(Wallet wallet, WalletTransactionType type, BigDecimal amount, String purpose) {
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setType(type);
        tx.setAmount(amount);
        tx.setPurpose(purpose);
        tx.setTransferId(UUID.randomUUID().toString());
        tx.setDate(LocalDate.now());
        walletTransactionRepository.save(tx);
    }
}
