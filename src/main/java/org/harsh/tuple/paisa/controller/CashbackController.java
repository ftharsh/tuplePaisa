package org.harsh.tuple.paisa.controller;

import lombok.RequiredArgsConstructor;
import org.harsh.tuple.paisa.model.Cashback;
import org.harsh.tuple.paisa.service.CashbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cashback")
@RequiredArgsConstructor
public class CashbackController {

    private final CashbackService cashbackService;

    //! View Cashback History
    @GetMapping("/history")
    public ResponseEntity<?> getCashbackHistory(@RequestParam String userId) {
        List<Cashback> cashbacks = cashbackService.getCashbackHistory(userId);
        return ResponseEntity.ok(cashbacks);
    }
}
