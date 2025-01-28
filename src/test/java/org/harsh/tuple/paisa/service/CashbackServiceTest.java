package org.harsh.tuple.paisa.service;

import org.harsh.tuple.paisa.exception.InvalidTransactionAmountException;
import org.harsh.tuple.paisa.exception.WalletNotFoundException;
import org.harsh.tuple.paisa.model.Cashback;
import org.harsh.tuple.paisa.model.Wallet;
import org.harsh.tuple.paisa.repository.CashbackRepository;
import org.harsh.tuple.paisa.repository.WalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CashbackServiceTest {

    @Mock
    private CashbackRepository cashbackRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private CashbackService cashbackService;

    private static String userId;
    private static Wallet wallet;

    @BeforeAll
    static void setUpConstants() {
        userId = "harsh123";
        wallet = Wallet.builder()
                .id("harsh'swallet22")
                .userId(userId)
                .balance(1000.0).build();
    }

    @BeforeEach
    void setUpMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void resetWalletBalance() {
        wallet.setBalance(1000.0);
    }

    @Test
    void applyCashback_validRechargeAmount_shouldSaveCashbackAndUpdateWallet() {
        double rechargeAmount = 2000.0;
        double expectedCashback = rechargeAmount * 0.05; // Dynamic cashback calculation

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        cashbackService.applyCashback(userId, rechargeAmount);

        // Verify cashback is saved with builder
        verify(cashbackRepository, times(1)).save(argThat(cashback ->
                cashback.getUserId().equals(userId) &&
                        cashback.getAmount() == expectedCashback &&
                        cashback.getTimestamp() != null
        ));

        // Verify wallet is updated
        assertEquals(1000.0 + expectedCashback, wallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void applyCashback_invalidRechargeAmount_shouldThrowException() {
        double invalidRechargeAmount = -50.0;

        assertThrows(InvalidTransactionAmountException.class,
                () -> cashbackService.applyCashback(userId, invalidRechargeAmount));

        verifyNoInteractions(cashbackRepository, walletRepository);
    }




    @Test
    void getCashbackHistory_validUserId_shouldReturnCashbackList() {
        List<Cashback> mockCashbackList = List.of(
                Cashback.builder()
                        .id("cashback1")
                        .userId(userId)
                        .amount(50.0)
                        .timestamp(LocalDateTime.now())
                        .build(),
                Cashback.builder()
                        .id("cashback2")
                        .userId(userId)
                        .amount(25.0)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        when(walletRepository.existsByUserId(userId)).thenReturn(true);
        when(cashbackRepository.findByUserId(userId)).thenReturn(mockCashbackList);

        List<Cashback> cashbackHistory = cashbackService.getCashbackHistory(userId);

        assertEquals(mockCashbackList.size(), cashbackHistory.size());
        assertEquals(mockCashbackList.get(0).getAmount(), cashbackHistory.get(0).getAmount());
        assertEquals(mockCashbackList.get(1).getAmount(), cashbackHistory.get(1).getAmount());
    }

    @Test
    void getCashbackHistory_walletNotFound_shouldThrowException() {
        when(walletRepository.existsByUserId(userId)).thenReturn(false);

        assertThrows(WalletNotFoundException.class,
                () -> cashbackService.getCashbackHistory(userId));

        verifyNoInteractions(cashbackRepository);
    }
}
