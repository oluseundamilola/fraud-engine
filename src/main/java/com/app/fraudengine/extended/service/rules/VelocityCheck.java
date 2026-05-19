package com.app.fraudengine.extended.service.rules;

import com.app.fraudengine.extended.DTOs.FraudRuleResult;
import com.app.fraudengine.extended.DTOs.TransactionEventDTO;
import com.app.fraudengine.extended.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VelocityCheck {

    private static final int VELOCITY_SCORE = 30;


    private final RedisService redisService;

    public FraudRuleResult velocityCheck(
        TransactionEventDTO transaction
    ) {

        FraudRuleResult result =
            new FraudRuleResult();

        String key =
            "VELOCITY:" + transaction.getFromAccount();

        Long count =
            redisService.incrementWithTTL(
                key,
                1
            );

        if (count != null && count >= 8) {

            result.setTriggered(true);

            result.setScore(90);

            result.setReason(
                "Velocity fraud detected (7+ transactions in 1 minutes)"
            );

            log.error(
                "[FRAUD-ENGINE] VELOCITY FRAUD | Account: {} | Count: {}",
                transaction.getFromAccount(),
                count
            );

            return result;
        }

        if (count != null && count >= 5) {

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
}
