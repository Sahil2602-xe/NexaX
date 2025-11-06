package com.project.demo.service;

import com.project.demo.model.User;
import com.project.demo.model.Withdrawal;

import java.math.BigDecimal;
import java.util.List;

public interface WithdrawalService {

    Withdrawal requestWithdrawal (BigDecimal amount, User user);

    Withdrawal proceedWithWithdrawal(Long withdrawalId, boolean accept) throws Exception;

    List<Withdrawal> getUsersWithdrawalHistory(User user);

    List<Withdrawal> getAllWithdrawalRequest();
}
