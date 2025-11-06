package com.project.demo.controller;

import com.project.demo.domain.WalletTransactionType;
import com.project.demo.model.User;
import com.project.demo.model.Wallet;
import com.project.demo.model.WalletTransaction;
import com.project.demo.model.Withdrawal;
import com.project.demo.repository.WalletRepository;
import com.project.demo.repository.WithdrawalRepository;
import com.project.demo.service.UserService;
import com.project.demo.service.WalletService;
import com.project.demo.service.TransactionService;
import com.project.demo.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
public class WithdrawalController {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/api/withdrawal/{amount}")
    public ResponseEntity<?> withdrawalRequest(
            @PathVariable BigDecimal amount,
            @RequestHeader("Authorization") String jwt)throws Exception{
        User user = userService.findUserProfileByJwt(jwt);
        Wallet userWallet = walletService.getUserWallet(user);

        Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount, user);
        userWallet.setBalance(userWallet.getBalance().subtract(withdrawal.getAmount()));
        walletRepository.save(userWallet);

        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setWallet(userWallet);
        walletTransaction.setType(WalletTransactionType.WITHDRAWAL);
        walletTransaction.setPurpose("Bank Account Withdrawal");
        walletTransaction.setAmount(withdrawal.getAmount());
        walletTransaction.setDate(LocalDate.now());

        transactionService.saveTransaction(walletTransaction);

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @PatchMapping("/api/admin/withdrawal/{id}/proceed/{accept}")
    public ResponseEntity<?> proceedWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization") String jwt) throws Exception{
        User user = userService.findUserProfileByJwt(jwt);

        Withdrawal withdrawal = withdrawalService.proceedWithWithdrawal(id, accept);

        Wallet userWallet = walletService.getUserWallet(user);
        if(!accept){
            walletService.addBalance(userWallet, withdrawal.getAmount());
        }
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @GetMapping("/api/withdrawal")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalHistory(
            @RequestHeader("Authorization") String jwt) throws Exception{
        User user = userService.findUserProfileByJwt(jwt);

        List<Withdrawal> withdrawal = withdrawalService.getUsersWithdrawalHistory(user);

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @GetMapping("/api/admin/withdrawal")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalRequest(
            @RequestHeader("Authorization")String jwt)throws Exception{
        User user = userService.findUserProfileByJwt(jwt);

        List<Withdrawal> withdrawal = withdrawalService.getAllWithdrawalRequest();

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

}
