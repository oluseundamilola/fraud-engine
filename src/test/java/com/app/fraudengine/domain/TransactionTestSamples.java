package com.app.fraudengine.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Transaction getTransactionSample1() {
        return new Transaction()
            .id(1L)
            .transactionReference("transactionReference1")
            .fromAccount("fromAccount1")
            .toAccount("toAccount1")
            .transactionType("transactionType1")
            .status("status1")
            .location("location1")
            .ipAddress("ipAddress1")
            .deviceId("deviceId1")
            .narration("narration1")
            .fraudScore(1)
            .reason("reason1");
    }

    public static Transaction getTransactionSample2() {
        return new Transaction()
            .id(2L)
            .transactionReference("transactionReference2")
            .fromAccount("fromAccount2")
            .toAccount("toAccount2")
            .transactionType("transactionType2")
            .status("status2")
            .location("location2")
            .ipAddress("ipAddress2")
            .deviceId("deviceId2")
            .narration("narration2")
            .fraudScore(2)
            .reason("reason2");
    }

    public static Transaction getTransactionRandomSampleGenerator() {
        return new Transaction()
            .id(longCount.incrementAndGet())
            .transactionReference(UUID.randomUUID().toString())
            .fromAccount(UUID.randomUUID().toString())
            .toAccount(UUID.randomUUID().toString())
            .transactionType(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .location(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString())
            .deviceId(UUID.randomUUID().toString())
            .narration(UUID.randomUUID().toString())
            .fraudScore(intCount.incrementAndGet())
            .reason(UUID.randomUUID().toString());
    }
}
