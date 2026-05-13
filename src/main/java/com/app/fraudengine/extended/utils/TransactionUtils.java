package com.app.fraudengine.extended.utils;

import com.app.fraudengine.domain.enumeration.TransactionStatus;
import com.app.fraudengine.service.TransactionQueryService;
import com.app.fraudengine.service.criteria.TransactionCriteria;
import com.app.fraudengine.service.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.StringFilter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionUtils {

    private final TransactionQueryService transactionQueryService;

    /**
     * Load all transactions by account number
     */
    public List<TransactionDTO> getTransactionsByFromAccount(
        String accountNumber
    ) {

        TransactionCriteria criteria =
            new TransactionCriteria();

        StringFilter fromAccountFilter =
            new StringFilter();

        fromAccountFilter.setEquals(accountNumber);

        criteria.setFromAccount(fromAccountFilter);

        Pageable pageable =
            PageRequest.of(0, 1000);

        return transactionQueryService
            .findByCriteria(criteria, pageable)
            .getContent();
    }

    /**
     * Load successful transactions only
     */
    public List<TransactionDTO> getSuccessfulTransactionsByFromAccount(
        String accountNumber
    ) {

        TransactionCriteria criteria =
            new TransactionCriteria();

        StringFilter fromAccountFilter =
            new StringFilter();

        fromAccountFilter.setEquals(accountNumber);

        criteria.setFromAccount(fromAccountFilter);

        StringFilter statusFilter = new StringFilter();

        statusFilter.setEquals("SUCCESS");

        criteria.setStatus(statusFilter);

        Pageable pageable =
            PageRequest.of(0, 1000);

        return transactionQueryService
            .findByCriteria(criteria, pageable)
            .getContent();
    }

    /**
     * Load recent transactions for velocity checks
     */
    public List<TransactionDTO> getRecentTransactions(
        String accountNumber,
        Instant startTime
    ) {

        TransactionCriteria criteria =
            new TransactionCriteria();

        StringFilter fromAccountFilter =
            new StringFilter();

        fromAccountFilter.setEquals(accountNumber);

        criteria.setFromAccount(fromAccountFilter);

        InstantFilter createdAtFilter =
            new InstantFilter();

        createdAtFilter.setGreaterThanOrEqual(startTime);

        criteria.setCreatedAt(createdAtFilter);

        Pageable pageable =
            PageRequest.of(0, 1000);

        return transactionQueryService
            .findByCriteria(criteria, pageable)
            .getContent();
    }

    /**
     * Load transactions by device ID
     */
    public List<TransactionDTO> getTransactionsByDeviceId(
        String deviceId
    ) {

        TransactionCriteria criteria =
            new TransactionCriteria();

        StringFilter deviceFilter =
            new StringFilter();

        deviceFilter.setEquals(deviceId);

        criteria.setDeviceId(deviceFilter);

        Pageable pageable =
            PageRequest.of(0, 1000);

        return transactionQueryService
            .findByCriteria(criteria, pageable)
            .getContent();
    }

    /**
     * Count recent transactions for fraud velocity rules
     */
    public int countRecentTransactions(
        String accountNumber,
        Instant startTime
    ) {

        return getRecentTransactions(
            accountNumber,
            startTime
        ).size();
    }

    /**
     * Get average transaction amount for an account
     */
    public BigDecimal getAverageTransactionAmount(String accountNumber) {

        List<TransactionDTO> transactions = getTransactionsByFromAccount(accountNumber);

        if (transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = transactions.stream()
            .map(TransactionDTO::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(transactions.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Check if device is new for this account
     */
    public boolean isNewDevice(String accountNumber, String deviceId) {

        List<TransactionDTO> transactions = getTransactionsByFromAccount(accountNumber);

        return transactions.stream()
            .noneMatch(t -> deviceId != null && deviceId.equals(t.getDeviceId()));
    }

    /**
     * Get distinct locations used by account
     */
    public List<String> getKnownLocations(String accountNumber) {

        return getTransactionsByFromAccount(accountNumber)
            .stream()
            .map(TransactionDTO::getLocation)
            .filter(l -> l != null && !l.isEmpty())
            .distinct()
            .toList();
    }

    /**
     * Count total transactions for account
     */
    public int countTransactions(String accountNumber) {

        return getTransactionsByFromAccount(accountNumber)
            .size();
    }
}
