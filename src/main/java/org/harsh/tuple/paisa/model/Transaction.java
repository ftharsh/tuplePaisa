package org.harsh.tuple.paisa.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;
    private String userId;
    private String senderId;
    private String recipientUsername;
    private String recipientId;
    private String walletId;
    private TransactionType type;
    private double amount;
    private LocalDateTime timestamp ;
}
