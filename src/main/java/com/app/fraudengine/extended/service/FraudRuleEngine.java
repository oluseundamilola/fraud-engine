package com.app.fraudengine.extended.service;

import com.app.fraudengine.domain.enumeration.FraudColor;
import com.app.fraudengine.extended.DTOs.FraudRuleResult;
import com.app.fraudengine.extended.DTOs.InspectionResultDTO;
import com.app.fraudengine.extended.DTOs.TransactionEventDTO;
import com.app.fraudengine.extended.events.PublisherService;
import com.app.fraudengine.extended.service.rules.HighAmountCheck;
import com.app.fraudengine.extended.service.rules.NewDeviceAndLocationCheck;
import com.app.fraudengine.extended.service.rules.VelocityCheck;
import com.app.fraudengine.extended.utils.TransactionUtils;
import com.app.fraudengine.service.TransactionService;
import com.app.fraudengine.service.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudRuleEngine {

    private static final int HIGH_AMOUNT_SCORE = 35;
    private static final int VERY_HIGH_AMOUNT_SCORE = 60;

    @Autowired
    private TransactionUtils transactionUtils;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private LiveTransactionStreamService
        liveTransactionStreamService;

    @Autowired
    private HighAmountCheck highAmountCheck;

    private final NewDeviceAndLocationCheck newDeviceAndLocationCheck;
    private final VelocityCheck velocityCheck;




    public void redLevelInspection(
        TransactionEventDTO event
    ) {

        TransactionDTO transaction =
            buildTransactionToSave(event);

        List<FraudRuleResult> ruleResults =
            new ArrayList<>();

        // =========================
        // RUN RULES
        // =========================
        ruleResults.add(
            highAmountCheck.highAmountCheck(event)
        );

        ruleResults.add(
            newDeviceAndLocationCheck.newDeviceCheck(event)
        );

        ruleResults.add(
            newDeviceAndLocationCheck.locationCheck(event)
        );

        ruleResults.add(
            velocityCheck.velocityCheck(event)
        );

        // =========================
        // CALCULATE FINAL SCORE
        // =========================
        int totalScore = calculateTotalScore(ruleResults);

        String reasons =
            buildReasons(ruleResults);

        // =========================
        // SET FRAUD VALUES
        // =========================
        transaction.setFraudScore(totalScore);

        transaction.setReason(reasons);

        applyFraudDecision(
            transaction,
            totalScore
        );

        // =========================
        // SAVE
        // =========================
        transactionService.save(transaction);

        liveTransactionStreamService
            .publishTransaction(transaction);


    }




    /**
     * CALCULATE TOTAL SCORE
     */
    private int calculateTotalScore(
        List<FraudRuleResult> results
    ) {

        int total =  results.stream()
            .filter(FraudRuleResult::isTriggered)
            .mapToInt(FraudRuleResult::getScore)
            .sum();
        return Math.min(total, 100);
    }

    /**
     * BUILD REASON STRING
     */
    private String buildReasons(
        List<FraudRuleResult> results
    ) {

        StringBuilder reasons =
            new StringBuilder();

        results.stream()
            .filter(FraudRuleResult::isTriggered)
            .forEach(result ->
                reasons.append(result.getReason())
                    .append(". ")
            );

        return reasons.toString();
    }

    /**
     * APPLY FINAL FRAUD DECISION
     */
    private void applyFraudDecision(
        TransactionDTO transaction,
        int score
    ) {

        if (score >= 80) {

            transaction.setColor(FraudColor.RED);

            transaction.setBlocked(true);

            InspectionResultDTO result =
                new InspectionResultDTO(
                    transaction.getTransactionReference(),
                    true,
                    FraudColor.RED.name(),
                    score,
                    "Fraud score exceeded safe threshold"
                );

            publisherService.publishInspectionResult(result);

        } else if (score >= 50) {

            transaction.setColor(FraudColor.ORANGE);

            transaction.setBlocked(false);

        } else if (score >= 20) {

            transaction.setColor(FraudColor.YELLOW);

            transaction.setBlocked(false);

        } else {

            transaction.setColor(FraudColor.GREEN);

            transaction.setBlocked(false);
        }
    }

    /**
     * BUILD TRANSACTION DTO
     */
    private TransactionDTO buildTransactionToSave(
        TransactionEventDTO event
    ) {

        TransactionDTO transaction =
            new TransactionDTO();

        transaction.setTransactionReference(
            event.getTransactionReference()
        );

        transaction.setFromAccount(
            event.getFromAccount()
        );

        transaction.setToAccount(
            event.getToAccount()
        );

        transaction.setAmount(
            event.getAmount()
        );

        transaction.setTransactionType(
            event.getTransactionType()
        );

        transaction.setStatus(
            event.getStatus()
        );

        transaction.setLocation(
            event.getLocation()
        );

        transaction.setIpAddress(
            event.getIpAddress()
        );

        transaction.setCreatedAt(
            event.getCreatedAt()
        );

        transaction.setDeviceId(
            event.getDeviceId()
        );

        transaction.setNarration(
            event.getNarration()
        );

        transaction.setFraudScore(0);

        transaction.setBlocked(false);

        transaction.setReason("");

        transaction.setColor(FraudColor.GREEN);

        return transaction;
    }
}
