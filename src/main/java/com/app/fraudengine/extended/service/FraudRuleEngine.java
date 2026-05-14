package com.app.fraudengine.extended.service;

import com.app.fraudengine.domain.enumeration.FraudColor;
import com.app.fraudengine.extended.DTOs.FraudRuleResult;
import com.app.fraudengine.extended.DTOs.InspectionResultDTO;
import com.app.fraudengine.extended.DTOs.TransactionEventDTO;
import com.app.fraudengine.extended.events.PublisherService;
import com.app.fraudengine.extended.utils.TransactionUtils;
import com.app.fraudengine.service.TransactionService;
import com.app.fraudengine.service.dto.TransactionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FraudRuleEngine {

    private static final int HIGH_AMOUNT_SCORE = 80;
    private static final int NEW_DEVICE_SCORE = 30;
    private static final int NEW_LOCATION_SCORE = 30;
    private static final int VELOCITY_SCORE = 40;
    private static final int VERY_HIGH_AMOUNT_SCORE = 100;

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

    public TransactionDTO redLevelInspection(
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
            highAmountCheck(event)
        );

        ruleResults.add(
            newDeviceCheck(event)
        );

        ruleResults.add(
            locationCheck(event)
        );

        ruleResults.add(
            velocityCheck(event)
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

        return transaction;
    }

    /**
     * HIGH AMOUNT RULE
     */
    private FraudRuleResult highAmountCheck(
        TransactionEventDTO transaction
    ) {

        FraudRuleResult result =
            new FraudRuleResult();

        String account =
            transaction.getFromAccount();

        BigDecimal currentAmount =
            transaction.getAmount();

        int transactionCount =
            transactionUtils.countTransactions(account);

        // Ignore accounts with little history
        if (transactionCount < 10) {
            return result;
        }

        BigDecimal avgAmount =
            transactionUtils.getAverageTransactionAmount(account);

        if (avgAmount == null ||
            avgAmount.compareTo(BigDecimal.ZERO) <= 0) {

            return result;
        }

        /*
         * NORMAL HIGH THRESHOLD
         * Example:
         * Avg = 10k
         * Threshold = 20k
         */
        BigDecimal highThreshold =
            avgAmount.multiply(BigDecimal.valueOf(2));

        /*
         * VERY HIGH / EXTREME THRESHOLD
         * Example:
         * Avg = 10k
         * Threshold = 200k
         */
        BigDecimal veryHighThreshold =
            avgAmount.multiply(BigDecimal.valueOf(5));

        // ------------------------------------------------
        // VERY HIGH / EXTREME AMOUNT
        // ------------------------------------------------
        if (
            currentAmount.compareTo(veryHighThreshold) > 0 ||

                // Hard ceiling protection
                currentAmount.compareTo(
                    BigDecimal.valueOf(200000)
                ) > 0
        ) {

            result.setTriggered(true);

            result.setScore(VERY_HIGH_AMOUNT_SCORE);

            result.setReason(
                "Extreme transaction amount anomaly detected"
            );

            log.error(
                "[FRAUD-ENGINE] EXTREME RED FLAG | Account: {} | Avg: {} | Current: {}",
                account,
                avgAmount,
                currentAmount
            );

            return result;
        }

        // ------------------------------------------------
        // HIGH AMOUNT
        // ------------------------------------------------
        if (currentAmount.compareTo(highThreshold) > 0) {

            result.setTriggered(true);

            result.setScore(HIGH_AMOUNT_SCORE);

            result.setReason(
                "High amount anomaly detected"
            );

            log.warn(
                "[FRAUD-ENGINE] High amount anomaly | Account: {} | Avg: {} | Current: {}",
                account,
                avgAmount,
                currentAmount
            );
        }

        return result;
    }

    /**
     * NEW DEVICE RULE
     */
    private FraudRuleResult newDeviceCheck(
        TransactionEventDTO transaction
    ) {

        FraudRuleResult result =
            new FraudRuleResult();

        String account =
            transaction.getFromAccount();

        int transactionCount =
            transactionUtils.countTransactions(account);

        // Ignore accounts with little history
        if (transactionCount < 3) {
            return result;
        }

        boolean isNewDevice =
            transactionUtils.isNewDevice(
                transaction.getFromAccount(),
                transaction.getDeviceId()
            );

        if (isNewDevice) {

            result.setTriggered(true);

            result.setScore(NEW_DEVICE_SCORE);

            result.setReason("New device detected");

            log.warn(
                "RED FLAG: New device for {}",
                transaction.getFromAccount()
            );
        }

        return result;
    }

    /**
     * LOCATION RULE
     */
    private FraudRuleResult locationCheck(
        TransactionEventDTO transaction
    ) {

        FraudRuleResult result =
            new FraudRuleResult();

        List<String> knownLocations =
            transactionUtils.getKnownLocations(
                transaction.getFromAccount()
            );

        if (
            transaction.getLocation() != null &&
                !knownLocations.isEmpty() &&
                !knownLocations.contains(transaction.getLocation())
        ) {

            result.setTriggered(true);

            result.setScore(NEW_LOCATION_SCORE);

            result.setReason("Unusual location");

            log.warn(
                "RED FLAG: New location for {}",
                transaction.getFromAccount()
            );
        }

        return result;
    }

    /**
     * VELOCITY RULE
     */
    private FraudRuleResult velocityCheck(
        TransactionEventDTO transaction
    ) {

        FraudRuleResult result =
            new FraudRuleResult();

        String key =
            "VELOCITY:" + transaction.getFromAccount();

        Long count =
            redisService.incrementWithTTL(
                key,
                10
            );

        if (count != null && count >= 3) {

            result.setTriggered(true);

            result.setScore(90);

            result.setReason(
                "Velocity fraud detected (3+ transactions in 4 minutes)"
            );

            log.error(
                "[FRAUD-ENGINE] VELOCITY FRAUD | Account: {} | Count: {}",
                transaction.getFromAccount(),
                count
            );

            return result;
        }

        if (count != null && count >= 2) {

            result.setTriggered(true);

            result.setScore(VELOCITY_SCORE);

            result.setReason(
                "High transaction velocity spike detected"
            );

            log.warn(
                "[FRAUD-ENGINE] Velocity warning | Account: {} | Count: {}",
                transaction.getFromAccount(),
                count
            );
        }

        return result;
    }

    /**
     * CALCULATE TOTAL SCORE
     */
    private int calculateTotalScore(
        List<FraudRuleResult> results
    ) {

        return results.stream()
            .filter(FraudRuleResult::isTriggered)
            .mapToInt(FraudRuleResult::getScore)
            .sum();
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

        if (score >= 100) {

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

        } else if (score >= 90) {

            transaction.setColor(FraudColor.ORANGE);

            transaction.setBlocked(false);

        } else if (score >= 10) {

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
