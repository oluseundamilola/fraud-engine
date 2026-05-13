package com.app.fraudengine.service;

import com.app.fraudengine.domain.*; // for static metamodels
import com.app.fraudengine.domain.Transaction;
import com.app.fraudengine.repository.TransactionRepository;
import com.app.fraudengine.service.criteria.TransactionCriteria;
import com.app.fraudengine.service.dto.TransactionDTO;
import com.app.fraudengine.service.mapper.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Transaction} entities in the database.
 * The main input is a {@link TransactionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TransactionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionQueryService extends QueryService<Transaction> {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionQueryService.class);

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    public TransactionQueryService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Return a {@link Page} of {@link TransactionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionDTO> findByCriteria(TransactionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Transaction> specification = createSpecification(criteria);
        return transactionRepository.findAll(specification, page).map(transactionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Transaction> specification = createSpecification(criteria);
        return transactionRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Transaction> createSpecification(TransactionCriteria criteria) {
        Specification<Transaction> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Transaction_.id),
                buildStringSpecification(criteria.getTransactionReference(), Transaction_.transactionReference),
                buildStringSpecification(criteria.getFromAccount(), Transaction_.fromAccount),
                buildStringSpecification(criteria.getToAccount(), Transaction_.toAccount),
                buildRangeSpecification(criteria.getAmount(), Transaction_.amount),
                buildStringSpecification(criteria.getTransactionType(), Transaction_.transactionType),
                buildStringSpecification(criteria.getStatus(), Transaction_.status),
                buildStringSpecification(criteria.getLocation(), Transaction_.location),
                buildStringSpecification(criteria.getIpAddress(), Transaction_.ipAddress),
                buildRangeSpecification(criteria.getCreatedAt(), Transaction_.createdAt),
                buildStringSpecification(criteria.getDeviceId(), Transaction_.deviceId),
                buildStringSpecification(criteria.getNarration(), Transaction_.narration),
                buildRangeSpecification(criteria.getFraudScore(), Transaction_.fraudScore),
                buildSpecification(criteria.getBlocked(), Transaction_.blocked),
                buildStringSpecification(criteria.getReason(), Transaction_.reason),
                buildSpecification(criteria.getColor(), Transaction_.color)
            );
        }
        return specification;
    }
}
