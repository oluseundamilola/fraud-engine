package com.app.fraudengine.extended.service;

import com.app.fraudengine.domain.Transaction;
import com.app.fraudengine.extended.DTOs.TransactionEventDTO;
import com.app.fraudengine.extended.utils.TransactionUtils;
import com.app.fraudengine.repository.TransactionRepository;
import com.app.fraudengine.service.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoliceService {

    private final TransactionUtils transactionUtils;
    private final TransactionRepository transactionRepository;

  public List<TransactionDTO> getTransactionsByFromAccount(String accountNumber){
      return transactionUtils.getTransactionsByFromAccount(accountNumber);
  }


    public Page<TransactionDTO> searchTransactions(
        String accountNumber,
        LocalDate date,
        String status,
        String color,
        String transactionType,
        Pageable pageable
    ) {

        return transactionRepository
            .findAll((root, query, cb) -> {

                List<Predicate> predicates = new ArrayList<>();

                // account filter
                if (accountNumber != null && !accountNumber.isEmpty()) {
                    predicates.add(cb.equal(root.get("fromAccount"), accountNumber));
                }

                // date filter (IMPORTANT: day range)
                if (date != null) {
                    LocalDateTime startOfDay = date.atStartOfDay();
                    LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

                    predicates.add(
                        cb.between(root.get("createdAt"), startOfDay, endOfDay)
                    );
                }

                // status filter
                if (status != null && !status.isEmpty()) {
                    predicates.add(cb.equal(root.get("status"), status));
                }

                // color filter
                if (color != null && !color.isEmpty()) {
                    predicates.add(cb.equal(root.get("color"), color));
                }

                // transaction type filter
                if (transactionType != null && !transactionType.isEmpty()) {
                    predicates.add(cb.equal(root.get("transactionType"), transactionType));
                }

                return cb.and(predicates.toArray(new Predicate[0]));

            }, pageable)
            .map(this::mapToDTO);
    }

    private TransactionDTO mapToDTO(Transaction tx) {

        TransactionDTO dto = new TransactionDTO();

        dto.setId(tx.getId());
        dto.setTransactionReference(tx.getTransactionReference());
        dto.setFromAccount(tx.getFromAccount());
        dto.setToAccount(tx.getToAccount());
        dto.setAmount(tx.getAmount());
        dto.setTransactionType(tx.getTransactionType());
        dto.setStatus(tx.getStatus());
        dto.setLocation(tx.getLocation());
        dto.setIpAddress(tx.getIpAddress());
        dto.setCreatedAt(tx.getCreatedAt());
        dto.setDeviceId(tx.getDeviceId());
        dto.setNarration(tx.getNarration());
        dto.setFraudScore(tx.getFraudScore());
        dto.setBlocked(tx.getBlocked());
        dto.setReason(tx.getReason());
        dto.setColor(tx.getColor());

        return dto;
    }
}
