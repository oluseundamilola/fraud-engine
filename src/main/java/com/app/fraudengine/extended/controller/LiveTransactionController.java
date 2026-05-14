package com.app.fraudengine.extended.controller;


import com.app.fraudengine.extended.service.LiveTransactionStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@CrossOrigin(origins = "http://localhost:5173/")
@RestController
@RequiredArgsConstructor
public class LiveTransactionController {

    private final LiveTransactionStreamService
        liveTransactionStreamService;

    @GetMapping("/api/fraud/live-transactions")
    public SseEmitter streamTransactions() {

        return liveTransactionStreamService.subscribe();
    }
}
