package com.app.fraudengine.extended.service.rules;

import com.app.fraudengine.extended.DTOs.FraudRuleResult;
import com.app.fraudengine.extended.DTOs.TransactionEventDTO;
import com.app.fraudengine.extended.utils.TransactionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class NewDeviceAndLocationCheck {

    private static final int NEW_DEVICE_SCORE = 20;
    private static final int NEW_LOCATION_SCORE = 20;

    @Autowired
    private TransactionUtils transactionUtils;

    public FraudRuleResult newDeviceCheck(
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

    public FraudRuleResult locationCheck(
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
}
