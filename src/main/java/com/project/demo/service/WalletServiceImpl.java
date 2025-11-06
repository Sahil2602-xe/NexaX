package com.project.demo.service;

import com.project.demo.domain.OrderType;
import com.project.demo.domain.WalletTransactionType;
import com.project.demo.model.Order;
import com.project.demo.model.User;
import com.project.demo.model.Wallet;
import com.project.demo.model.WalletTransaction;
import com.project.demo.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionService transactionService;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(BigDecimal.ZERO);
            walletRepository.save(wallet);
        }
        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, BigDecimal money) {
        // Update wallet balance first
        wallet.setBalance(wallet.getBalance().add(money));
        walletRepository.save(wallet);

        // Create transaction with correct balance
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setType(WalletTransactionType.ADD_MONEY);
        tx.setAmount(money);
        tx.setBalanceAfterTransaction(wallet.getBalance());
        tx.setDate(LocalDate.now());
        tx.setPurpose("Wallet Top-up");
        transactionService.saveTransaction(tx);

        return wallet;
    }

    @Override
    public Wallet findWalletById(Long id) throws Exception {
        Optional<Wallet> wallet = walletRepository.findById(id);
        if (wallet.isPresent()) return wallet.get();
        throw new Exception("Wallet Not Found");
    }

    @Override
    public Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, BigDecimal amount) throws Exception {
        Wallet senderWallet = getUserWallet(sender);

        if (senderWallet.getBalance().compareTo(amount) < 0)
            throw new Exception("Insufficient Balance");

        // Update balances
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        walletRepository.save(senderWallet);

        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        walletRepository.save(receiverWallet);

        // Sender transaction
        WalletTransaction senderTx = new WalletTransaction();
        senderTx.setWallet(senderWallet);
        senderTx.setType(WalletTransactionType.WALLET_TRANSFER);
        senderTx.setAmount(amount);
        senderTx.setBalanceAfterTransaction(senderWallet.getBalance());
        senderTx.setDate(LocalDate.now());
        senderTx.setPurpose("Sent to Wallet ID: " + receiverWallet.getId());
        transactionService.saveTransaction(senderTx);

        // Receiver transaction
        WalletTransaction receiverTx = new WalletTransaction();
        receiverTx.setWallet(receiverWallet);
        receiverTx.setType(WalletTransactionType.WALLET_TRANSFER);
        receiverTx.setAmount(amount);
        receiverTx.setBalanceAfterTransaction(receiverWallet.getBalance());
        receiverTx.setDate(LocalDate.now());
        receiverTx.setPurpose("Received from Wallet ID: " + senderWallet.getId());
        transactionService.saveTransaction(receiverTx);

        return senderWallet;
    }

    @Override
    public Wallet payOrderPayment(Order order, User user) throws Exception {
        Wallet wallet = getUserWallet(user);

        if (order.getOrderType().equals(OrderType.BUY)) {
            if (wallet.getBalance().compareTo(order.getPrice()) < 0)
                throw new Exception("Insufficient Funds for Transaction");

            wallet.setBalance(wallet.getBalance().subtract(order.getPrice()));

            // BUY transaction
            WalletTransaction tx = new WalletTransaction();
            tx.setWallet(wallet);
            tx.setType(WalletTransactionType.BUY_ASSET);
            tx.setAmount(order.getPrice());
            tx.setBalanceAfterTransaction(wallet.getBalance());
            tx.setDate(LocalDate.now());
            tx.setPurpose("Bought Asset - Order ID: " + order.getId());
            transactionService.saveTransaction(tx);
        } else {
            wallet.setBalance(wallet.getBalance().add(order.getPrice()));

            // SELL transaction
            WalletTransaction tx = new WalletTransaction();
            tx.setWallet(wallet);
            tx.setType(WalletTransactionType.SELL_ASSET);
            tx.setAmount(order.getPrice());
            tx.setBalanceAfterTransaction(wallet.getBalance());
            tx.setDate(LocalDate.now());
            tx.setPurpose("Sold Asset - Order ID: " + order.getId());
            transactionService.saveTransaction(tx);
        }

        walletRepository.save(wallet);
        return wallet;
    }

}
