package com.app.fraudengine.repository;

import com.app.fraudengine.domain.Transaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {}
