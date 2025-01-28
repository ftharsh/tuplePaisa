package org.harsh.tuple.paisa.service;

import lombok.RequiredArgsConstructor;
import org.harsh.tuple.paisa.exception.InvalidTransactionAmountException;
import org.harsh.tuple.paisa.exception.WalletNotFoundException;
import org.harsh.tuple.paisa.model.Cashback;
import org.harsh.tuple.paisa.model.Wallet;
import org.harsh.tuple.paisa.repository.CashbackRepository;
import org.harsh.tuple.paisa.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class CashbackService {

    private final CashbackRepository cashbackRepository;
    private final WalletRepository walletRepository;

    // *cashback Appied based on recharge amount and update wallet
    public void applyCashback(String userId, double rechargeAmount) {
        if (rechargeAmount <= 0) {
            throw new InvalidTransactionAmountException(rechargeAmount);
        }

        double cashbackAmount = calculateCashback(rechargeAmount);
        if (cashbackAmount > 0) {
            // Save cashback record
            Cashback cashback = Cashback.builder()
                    .userId(userId)
                    .amount(cashbackAmount)
                    .timestamp(LocalDateTime.now())
                    .build();

            cashbackRepository.save(cashback);

            // *Update the wallet balance with cashback amount
            Wallet wallet = walletRepository.findByUserId(userId)
                    .orElseThrow(() -> new WalletNotFoundException(userId));
            wallet.setBalance(wallet.getBalance() + cashbackAmount);
            walletRepository.save(wallet);
        }
    }

    //* Cashback (5% of recharge amount)
    private double calculateCashback(double rechargeAmount) {
        return rechargeAmount * 0.05; // Only calculate the cashback amount (5%)
    }

    // *Viewng Cashback History
    public List<Cashback> getCashbackHistory(String userId) {
        if (!walletRepository.existsByUserId(userId)) {
            throw new WalletNotFoundException(userId);
        }
        return cashbackRepository.findByUserId(userId);
    }
}
