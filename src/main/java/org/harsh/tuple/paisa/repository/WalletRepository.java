package org.harsh.tuple.paisa.repository;

import org.harsh.tuple.paisa.model.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WalletRepository extends MongoRepository<Wallet, String> {
    Optional<Wallet> findByUserId(String userId);
    void deleteByUserId(String walletId);
    boolean existsByUserId(String userId);

}
