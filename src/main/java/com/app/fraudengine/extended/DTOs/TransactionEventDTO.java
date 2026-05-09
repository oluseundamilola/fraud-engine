package com.app.fraudengine.extended.DTOs;


import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransactionEventDTO {

    private String transactionReference;

    private String fromAccount;

    private String toAccount;

    private BigDecimal amount;

    private String transactionType;

    private String status;

    private String location;

    private String ipAddress;

    private Instant createdAt;

    private String deviceId;
    private String narration;
}

