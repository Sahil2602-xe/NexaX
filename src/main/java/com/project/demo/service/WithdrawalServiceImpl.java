package com.project.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.demo.domain.WithdrawalStatus;
import com.project.demo.model.User;
import com.project.demo.model.Withdrawal;
import com.project.demo.repository.WithdrawalRepository;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Override
    public Withdrawal requestWithdrawal(BigDecimal amount, User user) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAmount(amount);
        withdrawal.setUser(user);
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setDate(LocalDateTime.now());

        return withdrawalRepository.save(withdrawal);
    }

    @Override
    public Withdrawal proceedWithWithdrawal(Long withdrawalId, boolean accept) throws Exception {
        Optional<Withdrawal> withdrawalOpt = withdrawalRepository.findById(withdrawalId);

        if (withdrawalOpt.isEmpty()) {
            throw new Exception(" Withdrawal not found with ID: " + withdrawalId);
        }

        Withdrawal withdrawal = withdrawalOpt.get();

        if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
            throw new Exception("⚠️ Withdrawal already processed: " + withdrawal.getStatus());
        }

        withdrawal.setDate(LocalDateTime.now());

        if (accept) {
            withdrawal.setStatus(WithdrawalStatus.SUCCESS);
        } else {
            withdrawal.setStatus(WithdrawalStatus.DECLINE);
        }

        Withdrawal updated = withdrawalRepository.save(withdrawal);

        System.out.println("✅ Withdrawal ID " + withdrawalId + " updated to " + updated.getStatus());
        return updated;
    }

    @Override
    public List<Withdrawal> getUsersWithdrawalHistory(User user) {
        return withdrawalRepository.findByUserId(user.getId());
    }

    @Override
    public List<Withdrawal> getAllWithdrawalRequest() {
        return withdrawalRepository.findAll();
    }
}
