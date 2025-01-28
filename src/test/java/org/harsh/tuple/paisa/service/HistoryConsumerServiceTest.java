package org.harsh.tuple.paisa.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryConsumerServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WalletService walletService;

    private HistoryConsumerService historyConsumerService;

    @BeforeEach
    void setUp() {
        historyConsumerService = new HistoryConsumerService(objectMapper, walletService);
    }

    @Test
    void consumeHistory_ValidMessage_ProcessesSuccessfully() throws Exception {
        // Arrange
        String message = "valid-json-message";
        List<Map<String, Object>> transactions = new ArrayList<>();
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("userId", "user123");
        transaction.put("amount", 100);
        transactions.add(transaction);

        when(objectMapper.readValue(eq(message), any(TypeReference.class)))
                .thenReturn(transactions);

        // Act
        historyConsumerService.consumeHistory(message);

        // Assert
        verify(walletService, times(1))
                .addHistory(eq("user123"), eq(transactions));
    }

    @Test
    void consumeHistory_EmptyTransactions_DoesNotCallWalletService() throws Exception {
        // Arrange
        String message = "empty-json-array";
        List<Map<String, Object>> emptyTransactions = new ArrayList<>();

        when(objectMapper.readValue(eq(message), any(TypeReference.class)))
                .thenReturn(emptyTransactions);

        // Act
        historyConsumerService.consumeHistory(message);

        // Assert
        verify(walletService, never()).addHistory(any(), any());
    }

    @Test
    void consumeHistory_ExceptionInProcessing_LogsError() throws Exception {
        // Arrange
        String message = "invalid-json";
        when(objectMapper.readValue(eq(message), any(TypeReference.class)))
                .thenThrow(new RuntimeException("JSON parsing error"));

        // Act
        historyConsumerService.consumeHistory(message);

        // Assert
        verify(walletService, never()).addHistory(any(), any());
    }
}