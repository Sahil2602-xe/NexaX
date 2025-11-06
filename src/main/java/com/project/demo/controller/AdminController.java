package com.project.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.model.Withdrawal;
import com.project.demo.service.WithdrawalService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private WithdrawalService withdrawalService;

    // ✅ Get all withdrawal requests
    @GetMapping("/withdrawals")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawals() {
        List<Withdrawal> withdrawals = withdrawalService.getAllWithdrawalRequest();
        return new ResponseEntity<>(withdrawals, HttpStatus.OK);
    }

    // ✅ Approve or reject withdrawal
    @PatchMapping("/withdrawals/{id}")
    public ResponseEntity<Withdrawal> updateWithdrawalStatus(
            @PathVariable Long id,
            @RequestParam String status) throws Exception {

        boolean accept = status.equalsIgnoreCase("APPROVED");
        Withdrawal updated = withdrawalService.proceedWithWithdrawal(id, accept);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
