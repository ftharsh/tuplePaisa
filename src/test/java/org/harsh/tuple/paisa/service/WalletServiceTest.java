package org.harsh.tuple.paisa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.harsh.tuple.paisa.model.*;
import org.harsh.tuple.paisa.repository.CashbackRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.harsh.tuple.paisa.exception.InsufficientBalanceException;
import org.harsh.tuple.paisa.exception.InvalidTransactionAmountException;
import org.harsh.tuple.paisa.exception.WalletNotFoundException;
import org.harsh.tuple.paisa.repository.TransactionRepository;
import org.harsh.tuple.paisa.repository.UserRepository;
import org.harsh.tuple.paisa.repository.WalletRepository;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CashbackService cashbackService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private CashbackRepository cashbackRepository;

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private WalletService walletService;

    private static Wallet senderWallet;
    private static Wallet recipientWallet;
    private static User recipientUser;

    @BeforeAll
    static void setUpAll() {
        senderWallet = Wallet.builder()
                .id("sender-wallet-id")
                .userId("sender-user-id")
                .balance(1000.0)
                .build();

        recipientWallet = Wallet.builder()
                .id("recipient-wallet-id")
                .userId("recipient-user-id")
                .balance(500.0)
                .build();

        recipientUser = User.builder()
                .id("recipient-user-id")
                .username("recipient-username")
                .email("recipient@example.com")
                .build();


    }
    @AfterEach
    void setUpEach() {
        senderWallet.setBalance(1000.0);
    }
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
    }

    @Test
    public void testRechargeWallet_Success() {
        // Arrange
       double amount = 1000.0;

        when(walletRepository.findByUserId(senderWallet.getUserId())).thenReturn(Optional.of(senderWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(senderWallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = walletService.rechargeWallet(senderWallet.getUserId(),amount);

        assertEquals(2000.0, senderWallet.getBalance() , 000.1);
        assertEquals(TransactionType.RECHARGE, result.getType());
        assertEquals(amount, result.getAmount());

        verify(walletRepository).save(senderWallet);
        verify(transactionRepository).save(any(Transaction.class));
        verify(cashbackService).applyCashback(senderWallet.getUserId(), amount);
    }

    @Test
    public void testRechargeWallet_NegativeAmount() {
        // Arrange
        String userId = "user-id";
        double amount = -500.0;

        // Act & Assert
        assertThrows(InvalidTransactionAmountException.class,
                () -> walletService.rechargeWallet(userId, amount));
    }

    @Test
    public void testRechargeWallet_WalletNotFound() {
        // Arrange
        String userId = "user-id";
        double amount = 500.0;

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(WalletNotFoundException.class,
                () -> walletService.rechargeWallet(userId, amount));
    }

    @Test
    public void testTransferWallet_InsufficientBalance() {
        // Arrange
        double amount = 2000.0;

        when(walletRepository.findByUserId(senderWallet.getUserId())).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserId(recipientWallet.getUserId())).thenReturn(Optional.of(recipientWallet));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> walletService.transferWallet(senderWallet.getUserId(), recipientWallet.getUserId(), amount));
    }
    @Test
    void transferWallet_Success() throws Exception {
        // Given
        double amount = 100.0;
        when(walletRepository.findByUserId(senderWallet.getUserId())).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserId(recipientWallet.getUserId())).thenReturn(Optional.of(recipientWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userRepository.findById(recipientUser.getId())).thenReturn(Optional.of(recipientUser));

        // Mock ObjectMapper for email details serialization
        when(objectMapper.writeValueAsString(any(EmailDetails.class))).thenReturn("{\"email\":\"test@example.com\"}");

        // When
        List<Transaction> results = walletService.transferWallet(senderWallet.getUserId(), recipientWallet.getUserId(), amount);

        // Then
        assertEquals(2, results.size());
        assertEquals(TransactionType.TRANSFER, results.get(0).getType());
        assertEquals(amount, results.get(0).getAmount());

        // Verify wallet updates
        verify(walletRepository, times(2)).save(any(Wallet.class));

        // Verify email was sent via Kafka
        verify(kafkaTemplate).send(eq("email-notifications"), any(String.class));

        // Verify balances were updated correctly
        assertEquals(900.0, senderWallet.getBalance()); // 1000 - 100
        assertEquals(600.0, recipientWallet.getBalance()); // 500 + 100
    }




    @Test
    public void testTransferWallet_NegativeAmount() {
        // Arrange
        String senderId = "sender-user-id";
        String recipientId = "recipient-user-id";
        double amount = -200.0;

        // Act & Assert
        assertThrows(InvalidTransactionAmountException.class,
                () -> walletService.transferWallet(senderId, recipientId, amount));
    }

    @Test
    public void testTransferWallet_SenderWalletNotFound() {
        // Arrange
        String senderId = "sender-user-id";
        String recipientId = "recipient-user-id";
        double amount = 200.0;

        when(walletRepository.findByUserId(senderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(WalletNotFoundException.class,
                () -> walletService.transferWallet(senderId, recipientId, amount));
    }

    @Test
    public void testTransferWallet_RecipientWalletNotFound() {
        // Arrange
        String senderId = "sender-user-id";
        String recipientId = "recipient-user-id";
        double amount = 200.0;

        when(walletRepository.findByUserId(senderId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserId(recipientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(WalletNotFoundException.class,
                () -> walletService.transferWallet(senderId, recipientId, amount));
    }

    @Test
    public void testGetBalance_Success() {
        // Arrange
        String userId = "user-id";
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(500.0)
                .build();

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        // Act
        double balance = walletService.getBalance(userId);

        // Assert
        assertEquals(500.0, balance, 0.001);
    }

    @Test
    public void testGetBalance_WalletNotFound() {
        // Arrange
        String userId = "user-id";
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(WalletNotFoundException.class,
                () -> walletService.getBalance(userId));
    }
    @Test
    void getCombinedHistory_Success() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(
                Transaction.builder().timestamp(LocalDateTime.now()).build()
        );
        List<Cashback> cashbacks = Arrays.asList(
                Cashback.builder().timestamp(LocalDateTime.now()).build()
        );
        when(transactionRepository.findByUserId(recipientUser.getId())).thenReturn(transactions);
        when(cashbackRepository.findByUserId(recipientUser.getId())).thenReturn(cashbacks);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        // When
        walletService.getCombinedHistory(recipientUser.getId());

        // Then
        verify(kafkaTemplate).send(eq("wallet-history"), any(String.class));
    }



    @Test
    void sendEmail_Success() throws Exception {
        // Given
        double amount = 100.0;
        when(userRepository.findById(recipientUser.getId())).thenReturn(Optional.of(recipientUser));
        when(objectMapper.writeValueAsString(any(EmailDetails.class))).thenReturn("{}");

        // When
        walletService.sendEmail(recipientUser.getId(), amount);

        // Then
        verify(kafkaTemplate).send(eq("email-notifications"), any(String.class));
    }

}