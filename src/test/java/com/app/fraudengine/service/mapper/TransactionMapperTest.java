package com.app.fraudengine.service.mapper;

import static com.app.fraudengine.domain.TransactionAsserts.*;
import static com.app.fraudengine.domain.TransactionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        transactionMapper = new TransactionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTransactionSample1();
        var actual = transactionMapper.toEntity(transactionMapper.toDto(expected));
        assertTransactionAllPropertiesEquals(expected, actual);
    }
}
