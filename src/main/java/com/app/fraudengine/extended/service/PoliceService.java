package com.app.fraudengine.extended.service;

import com.app.fraudengine.extended.DTOs.TransactionEventDTO;
import com.app.fraudengine.extended.utils.TransactionUtils;
import com.app.fraudengine.service.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoliceService {

    private final TransactionUtils transactionUtils;

    public void inspectTransaction(
        TransactionEventDTO event
    ) {

        log.info(
            """
            [POLICE-SERVICE]
            Inspecting Transaction
            Ref: {}
            From: {}
            To: {}
            Amount: {}
            """,
            event.getTransactionReference(),
            event.getFromAccount(),
            event.getToAccount(),
            event.getAmount()
        );

        /*
         * ------------------------------------------------
         * Load customer transaction history
         * ------------------------------------------------
         */
        List<TransactionDTO> customerHistory =
            transactionUtils.getTransactionsByFromAccount(
                event.getFromAccount()
            );

        log.info(
            "[POLICE-SERVICE] Customer History Loaded | Total Transactions: {}",
            customerHistory.size()
        );

        /*
         * ------------------------------------------------
         * Load device transaction history
         * ------------------------------------------------
         */
        List<TransactionDTO> deviceHistory =
            transactionUtils.getTransactionsByDeviceId(
                event.getDeviceId()
            );

        log.info(
            "[POLICE-SERVICE] Device History Loaded | Total Transactions: {}",
            deviceHistory.size()
        );

        /*
         * ------------------------------------------------
         * Load recent transactions (velocity check preparation)
         * ------------------------------------------------
         */
        List<TransactionDTO> recentTransactions =
            transactionUtils.getRecentTransactions(
                event.getFromAccount(),
                Instant.now().minusSeconds(60)
            );

        log.info(
            """
            [POLICE-SERVICE]
            Recent Transactions Loaded
            Account: {}
            Last 60 Seconds Count: {}
            """,
            event.getFromAccount(),
            recentTransactions.size()
        );

        /*
         * ------------------------------------------------
         * Fraud rules will be added here later
         * ------------------------------------------------
         */

        log.info(
            "[POLICE-SERVICE] Transaction inspection completed | Ref: {}",
            event.getTransactionReference()
        );
    }
}
