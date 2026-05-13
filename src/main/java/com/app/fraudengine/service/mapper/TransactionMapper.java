package com.app.fraudengine.service.mapper;

import com.app.fraudengine.domain.Transaction;
import com.app.fraudengine.service.dto.TransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Transaction} and its DTO {@link TransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {}
