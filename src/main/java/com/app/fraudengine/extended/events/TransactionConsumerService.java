package com.app.fraudengine.extended.events;


import com.app.fraudengine.extended.DTOs.TransactionEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionConsumerService {

    @KafkaListener(
        topics = "transactions",
        groupId = "fraud-engine-group-v2"
    )
    public void consumeTransactionEvent(
        TransactionEventDTO event
    ) {

        log.info(
            "[FRAUD-ENGINE] Transaction received | Ref: {} | Amount: {} | From: {} | To: {}",
            event.getTransactionReference(),
            event.getAmount(),
            event.getFromAccount(),
            event.getToAccount()
        );
    }
}
