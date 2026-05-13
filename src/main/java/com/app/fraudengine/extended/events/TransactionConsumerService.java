package com.app.fraudengine.extended.events;


import com.app.fraudengine.extended.DTOs.TransactionEventDTO;
import com.app.fraudengine.extended.service.FraudRuleEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionConsumerService {

    @Autowired
    private FraudRuleEngine fraudRuleEngine;

    @KafkaListener(
        topics = "transactions",
        groupId = "fraud-engine-group-v2"
    )
    public void consumeTransactionEvent(
        TransactionEventDTO event
    ) {

        fraudRuleEngine.redLevelInspection(event);

        log.info(
            "[FRAUD-ENGINE] Transaction received | Ref: {} | Amount: {} | From: {} | To: {}",
            event.getTransactionReference(),
            event.getAmount(),
            event.getFromAccount(),
            event.getToAccount()
        );
    }
}
