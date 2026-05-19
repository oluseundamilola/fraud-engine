package com.app.fraudengine.extended.service.rules;

import com.app.fraudengine.extended.DTOs.FraudRuleResult;
import com.app.fraudengine.extended.DTOs.TransactionEventDTO;
import com.app.fraudengine.extended.utils.TransactionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class HighAmountCheck {

    private static final int HIGH_AMOUNT_SCORE = 35;
    private static final int VERY_HIGH_AMOUNT_SCORE = 60;


    @Autowired
    private TransactionUtils transactionUtils;

    public FraudRuleResult highAmountCheck(
        TransactionEventDTO transaction
    ) {

        FraudRuleResult result = new FraudRuleResult();

        String account = transaction.getFromAccount();

        BigDecimal currentAmount = transaction.getAmount();
        BigDecimal customerBalance = transaction.getCustomerBalance();

        int transactionCount =
            transactionUtils.countTransactions(account);

        // Ignore new accounts with little history
        if (transactionCount < 10) {
            return result;
        }

        BigDecimal avgAmount =
            transactionUtils.getAverageTransactionAmount(account);

        if (avgAmount == null ||
            avgAmount.compareTo(BigDecimal.ZERO) <= 0) {

            return result;
        }

        // =============================
        // DYNAMIC THRESHOLDS
        // =============================

        BigDecimal highThreshold =
            avgAmount.multiply(BigDecimal.valueOf(2));

        BigDecimal veryHighThreshold =
            avgAmount.multiply(BigDecimal.valueOf(5));

        // =============================
        // BALANCE RISK ANALYSIS
        // =============================

        BigDecimal balanceUsagePercent =
            BigDecimal.ZERO;

        if (customerBalance != null &&
            customerBalance.compareTo(BigDecimal.ZERO) > 0) {

            balanceUsagePercent =
                currentAmount.multiply(BigDecimal.valueOf(100))
                    .divide(customerBalance, 2, RoundingMode.HALF_UP);
        }

        // =============================
        // EXTREME RISK
        // =============================

        boolean extremeAmount =
            currentAmount.compareTo(veryHighThreshold) > 0;

        boolean hardLimitExceeded =
            currentAmount.compareTo(
                BigDecimal.valueOf(200000)
            ) > 0;

        boolean drainingAccount =
            balanceUsagePercent.compareTo(
                BigDecimal.valueOf(80)
            ) > 0;

        if (
            extremeAmount ||
                hardLimitExceeded ||
                drainingAccount
        ) {

            result.setTriggered(true);

            result.setScore(VERY_HIGH_AMOUNT_SCORE);

            result.setReason(
                "Extreme transaction anomaly detected"
            );

            log.error(
                "[FRAUD-ENGINE] EXTREME RED FLAG | " +
                    "Account: {} | Avg: {} | Current: {} | " +
                    "Balance: {} | Usage: {}%",
                account,
                avgAmount,
                currentAmount,
                customerBalance,
                balanceUsagePercent
            );

            return result;
        }

        // =============================
        // HIGH RISK
        // =============================

        boolean highAmount =
            currentAmount.compareTo(highThreshold) > 0;

        boolean moderateBalanceDrain =
            balanceUsagePercent.compareTo(
                BigDecimal.valueOf(50)
            ) > 0;

        if (
            highAmount ||
                moderateBalanceDrain
        ) {

            result.setTriggered(true);

            result.setScore(HIGH_AMOUNT_SCORE);

            result.setReason(
                "High amount anomaly detected"
            );

            log.warn(
                "[FRAUD-ENGINE] HIGH AMOUNT ANOMALY | " +
                    "Account: {} | Avg: {} | Current: {} | " +
                    "Balance: {} | Usage: {}%",
                account,
                avgAmount,
                currentAmount,
                customerBalance,
                balanceUsagePercent
            );
        }

        return result;
    }
}
