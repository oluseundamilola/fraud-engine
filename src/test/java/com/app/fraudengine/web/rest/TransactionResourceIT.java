package com.app.fraudengine.web.rest;

import static com.app.fraudengine.domain.TransactionAsserts.*;
import static com.app.fraudengine.web.rest.TestUtil.createUpdateProxyForBean;
import static com.app.fraudengine.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.app.fraudengine.IntegrationTest;
import com.app.fraudengine.domain.Transaction;
import com.app.fraudengine.domain.enumeration.FraudColor;
import com.app.fraudengine.repository.TransactionRepository;
import com.app.fraudengine.service.dto.TransactionDTO;
import com.app.fraudengine.service.mapper.TransactionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionResourceIT {

    private static final String DEFAULT_TRANSACTION_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_FROM_ACCOUNT = "AAAAAAAAAA";
    private static final String UPDATED_FROM_ACCOUNT = "BBBBBBBBBB";

    private static final String DEFAULT_TO_ACCOUNT = "AAAAAAAAAA";
    private static final String UPDATED_TO_ACCOUNT = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_TRANSACTION_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DEVICE_ID = "AAAAAAAAAA";
    private static final String UPDATED_DEVICE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NARRATION = "AAAAAAAAAA";
    private static final String UPDATED_NARRATION = "BBBBBBBBBB";

    private static final Integer DEFAULT_FRAUD_SCORE = 1;
    private static final Integer UPDATED_FRAUD_SCORE = 2;
    private static final Integer SMALLER_FRAUD_SCORE = 1 - 1;

    private static final Boolean DEFAULT_BLOCKED = false;
    private static final Boolean UPDATED_BLOCKED = true;

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final FraudColor DEFAULT_COLOR = FraudColor.GREEN;
    private static final FraudColor UPDATED_COLOR = FraudColor.YELLOW;

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    private Transaction insertedTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity() {
        return new Transaction()
            .transactionReference(DEFAULT_TRANSACTION_REFERENCE)
            .fromAccount(DEFAULT_FROM_ACCOUNT)
            .toAccount(DEFAULT_TO_ACCOUNT)
            .amount(DEFAULT_AMOUNT)
            .transactionType(DEFAULT_TRANSACTION_TYPE)
            .status(DEFAULT_STATUS)
            .location(DEFAULT_LOCATION)
            .ipAddress(DEFAULT_IP_ADDRESS)
            .createdAt(DEFAULT_CREATED_AT)
            .deviceId(DEFAULT_DEVICE_ID)
            .narration(DEFAULT_NARRATION)
            .fraudScore(DEFAULT_FRAUD_SCORE)
            .blocked(DEFAULT_BLOCKED)
            .reason(DEFAULT_REASON)
            .color(DEFAULT_COLOR);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity() {
        return new Transaction()
            .transactionReference(UPDATED_TRANSACTION_REFERENCE)
            .fromAccount(UPDATED_FROM_ACCOUNT)
            .toAccount(UPDATED_TO_ACCOUNT)
            .amount(UPDATED_AMOUNT)
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .status(UPDATED_STATUS)
            .location(UPDATED_LOCATION)
            .ipAddress(UPDATED_IP_ADDRESS)
            .createdAt(UPDATED_CREATED_AT)
            .deviceId(UPDATED_DEVICE_ID)
            .narration(UPDATED_NARRATION)
            .fraudScore(UPDATED_FRAUD_SCORE)
            .blocked(UPDATED_BLOCKED)
            .reason(UPDATED_REASON)
            .color(UPDATED_COLOR);
    }

    @BeforeEach
    void initTest() {
        transaction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTransaction != null) {
            transactionRepository.delete(insertedTransaction);
            insertedTransaction = null;
        }
    }

    @Test
    @Transactional
    void createTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        var returnedTransactionDTO = om.readValue(
            restTransactionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TransactionDTO.class
        );

        // Validate the Transaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTransaction = transactionMapper.toEntity(returnedTransactionDTO);
        assertTransactionUpdatableFieldsEquals(returnedTransaction, getPersistedTransaction(returnedTransaction));

        insertedTransaction = returnedTransaction;
    }

    @Test
    @Transactional
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTransactionReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setTransactionReference(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFromAccountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setFromAccount(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkToAccountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setToAccount(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setAmount(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setCreatedAt(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBlockedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setBlocked(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactions() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionReference").value(hasItem(DEFAULT_TRANSACTION_REFERENCE)))
            .andExpect(jsonPath("$.[*].fromAccount").value(hasItem(DEFAULT_FROM_ACCOUNT)))
            .andExpect(jsonPath("$.[*].toAccount").value(hasItem(DEFAULT_TO_ACCOUNT)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TRANSACTION_TYPE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].deviceId").value(hasItem(DEFAULT_DEVICE_ID)))
            .andExpect(jsonPath("$.[*].narration").value(hasItem(DEFAULT_NARRATION)))
            .andExpect(jsonPath("$.[*].fraudScore").value(hasItem(DEFAULT_FRAUD_SCORE)))
            .andExpect(jsonPath("$.[*].blocked").value(hasItem(DEFAULT_BLOCKED)))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR.toString())));
    }

    @Test
    @Transactional
    void getTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.transactionReference").value(DEFAULT_TRANSACTION_REFERENCE))
            .andExpect(jsonPath("$.fromAccount").value(DEFAULT_FROM_ACCOUNT))
            .andExpect(jsonPath("$.toAccount").value(DEFAULT_TO_ACCOUNT))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.transactionType").value(DEFAULT_TRANSACTION_TYPE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.deviceId").value(DEFAULT_DEVICE_ID))
            .andExpect(jsonPath("$.narration").value(DEFAULT_NARRATION))
            .andExpect(jsonPath("$.fraudScore").value(DEFAULT_FRAUD_SCORE))
            .andExpect(jsonPath("$.blocked").value(DEFAULT_BLOCKED))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR.toString()));
    }

    @Test
    @Transactional
    void getTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        Long id = transaction.getId();

        defaultTransactionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTransactionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTransactionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionReference equals to
        defaultTransactionFiltering(
            "transactionReference.equals=" + DEFAULT_TRANSACTION_REFERENCE,
            "transactionReference.equals=" + UPDATED_TRANSACTION_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionReference in
        defaultTransactionFiltering(
            "transactionReference.in=" + DEFAULT_TRANSACTION_REFERENCE + "," + UPDATED_TRANSACTION_REFERENCE,
            "transactionReference.in=" + UPDATED_TRANSACTION_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionReference is not null
        defaultTransactionFiltering("transactionReference.specified=true", "transactionReference.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionReference contains
        defaultTransactionFiltering(
            "transactionReference.contains=" + DEFAULT_TRANSACTION_REFERENCE,
            "transactionReference.contains=" + UPDATED_TRANSACTION_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionReference does not contain
        defaultTransactionFiltering(
            "transactionReference.doesNotContain=" + UPDATED_TRANSACTION_REFERENCE,
            "transactionReference.doesNotContain=" + DEFAULT_TRANSACTION_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByFromAccountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fromAccount equals to
        defaultTransactionFiltering("fromAccount.equals=" + DEFAULT_FROM_ACCOUNT, "fromAccount.equals=" + UPDATED_FROM_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByFromAccountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fromAccount in
        defaultTransactionFiltering(
            "fromAccount.in=" + DEFAULT_FROM_ACCOUNT + "," + UPDATED_FROM_ACCOUNT,
            "fromAccount.in=" + UPDATED_FROM_ACCOUNT
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByFromAccountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fromAccount is not null
        defaultTransactionFiltering("fromAccount.specified=true", "fromAccount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByFromAccountContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fromAccount contains
        defaultTransactionFiltering("fromAccount.contains=" + DEFAULT_FROM_ACCOUNT, "fromAccount.contains=" + UPDATED_FROM_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByFromAccountNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fromAccount does not contain
        defaultTransactionFiltering(
            "fromAccount.doesNotContain=" + UPDATED_FROM_ACCOUNT,
            "fromAccount.doesNotContain=" + DEFAULT_FROM_ACCOUNT
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByToAccountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where toAccount equals to
        defaultTransactionFiltering("toAccount.equals=" + DEFAULT_TO_ACCOUNT, "toAccount.equals=" + UPDATED_TO_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByToAccountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where toAccount in
        defaultTransactionFiltering("toAccount.in=" + DEFAULT_TO_ACCOUNT + "," + UPDATED_TO_ACCOUNT, "toAccount.in=" + UPDATED_TO_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByToAccountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where toAccount is not null
        defaultTransactionFiltering("toAccount.specified=true", "toAccount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByToAccountContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where toAccount contains
        defaultTransactionFiltering("toAccount.contains=" + DEFAULT_TO_ACCOUNT, "toAccount.contains=" + UPDATED_TO_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByToAccountNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where toAccount does not contain
        defaultTransactionFiltering("toAccount.doesNotContain=" + UPDATED_TO_ACCOUNT, "toAccount.doesNotContain=" + DEFAULT_TO_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount equals to
        defaultTransactionFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount in
        defaultTransactionFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is not null
        defaultTransactionFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is greater than or equal to
        defaultTransactionFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is less than or equal to
        defaultTransactionFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is less than
        defaultTransactionFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is greater than
        defaultTransactionFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionType equals to
        defaultTransactionFiltering(
            "transactionType.equals=" + DEFAULT_TRANSACTION_TYPE,
            "transactionType.equals=" + UPDATED_TRANSACTION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionType in
        defaultTransactionFiltering(
            "transactionType.in=" + DEFAULT_TRANSACTION_TYPE + "," + UPDATED_TRANSACTION_TYPE,
            "transactionType.in=" + UPDATED_TRANSACTION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionType is not null
        defaultTransactionFiltering("transactionType.specified=true", "transactionType.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionType contains
        defaultTransactionFiltering(
            "transactionType.contains=" + DEFAULT_TRANSACTION_TYPE,
            "transactionType.contains=" + UPDATED_TRANSACTION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionType does not contain
        defaultTransactionFiltering(
            "transactionType.doesNotContain=" + UPDATED_TRANSACTION_TYPE,
            "transactionType.doesNotContain=" + DEFAULT_TRANSACTION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where status equals to
        defaultTransactionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where status in
        defaultTransactionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where status is not null
        defaultTransactionFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where status contains
        defaultTransactionFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where status does not contain
        defaultTransactionFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where location equals to
        defaultTransactionFiltering("location.equals=" + DEFAULT_LOCATION, "location.equals=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllTransactionsByLocationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where location in
        defaultTransactionFiltering("location.in=" + DEFAULT_LOCATION + "," + UPDATED_LOCATION, "location.in=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllTransactionsByLocationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where location is not null
        defaultTransactionFiltering("location.specified=true", "location.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByLocationContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where location contains
        defaultTransactionFiltering("location.contains=" + DEFAULT_LOCATION, "location.contains=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllTransactionsByLocationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where location does not contain
        defaultTransactionFiltering("location.doesNotContain=" + UPDATED_LOCATION, "location.doesNotContain=" + DEFAULT_LOCATION);
    }

    @Test
    @Transactional
    void getAllTransactionsByIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where ipAddress equals to
        defaultTransactionFiltering("ipAddress.equals=" + DEFAULT_IP_ADDRESS, "ipAddress.equals=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsByIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where ipAddress in
        defaultTransactionFiltering("ipAddress.in=" + DEFAULT_IP_ADDRESS + "," + UPDATED_IP_ADDRESS, "ipAddress.in=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsByIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where ipAddress is not null
        defaultTransactionFiltering("ipAddress.specified=true", "ipAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where ipAddress contains
        defaultTransactionFiltering("ipAddress.contains=" + DEFAULT_IP_ADDRESS, "ipAddress.contains=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsByIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where ipAddress does not contain
        defaultTransactionFiltering("ipAddress.doesNotContain=" + UPDATED_IP_ADDRESS, "ipAddress.doesNotContain=" + DEFAULT_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt equals to
        defaultTransactionFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt in
        defaultTransactionFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransactionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where createdAt is not null
        defaultTransactionFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByDeviceIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where deviceId equals to
        defaultTransactionFiltering("deviceId.equals=" + DEFAULT_DEVICE_ID, "deviceId.equals=" + UPDATED_DEVICE_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByDeviceIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where deviceId in
        defaultTransactionFiltering("deviceId.in=" + DEFAULT_DEVICE_ID + "," + UPDATED_DEVICE_ID, "deviceId.in=" + UPDATED_DEVICE_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByDeviceIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where deviceId is not null
        defaultTransactionFiltering("deviceId.specified=true", "deviceId.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByDeviceIdContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where deviceId contains
        defaultTransactionFiltering("deviceId.contains=" + DEFAULT_DEVICE_ID, "deviceId.contains=" + UPDATED_DEVICE_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByDeviceIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where deviceId does not contain
        defaultTransactionFiltering("deviceId.doesNotContain=" + UPDATED_DEVICE_ID, "deviceId.doesNotContain=" + DEFAULT_DEVICE_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByNarrationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where narration equals to
        defaultTransactionFiltering("narration.equals=" + DEFAULT_NARRATION, "narration.equals=" + UPDATED_NARRATION);
    }

    @Test
    @Transactional
    void getAllTransactionsByNarrationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where narration in
        defaultTransactionFiltering("narration.in=" + DEFAULT_NARRATION + "," + UPDATED_NARRATION, "narration.in=" + UPDATED_NARRATION);
    }

    @Test
    @Transactional
    void getAllTransactionsByNarrationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where narration is not null
        defaultTransactionFiltering("narration.specified=true", "narration.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByNarrationContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where narration contains
        defaultTransactionFiltering("narration.contains=" + DEFAULT_NARRATION, "narration.contains=" + UPDATED_NARRATION);
    }

    @Test
    @Transactional
    void getAllTransactionsByNarrationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where narration does not contain
        defaultTransactionFiltering("narration.doesNotContain=" + UPDATED_NARRATION, "narration.doesNotContain=" + DEFAULT_NARRATION);
    }

    @Test
    @Transactional
    void getAllTransactionsByFraudScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fraudScore equals to
        defaultTransactionFiltering("fraudScore.equals=" + DEFAULT_FRAUD_SCORE, "fraudScore.equals=" + UPDATED_FRAUD_SCORE);
    }

    @Test
    @Transactional
    void getAllTransactionsByFraudScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fraudScore in
        defaultTransactionFiltering(
            "fraudScore.in=" + DEFAULT_FRAUD_SCORE + "," + UPDATED_FRAUD_SCORE,
            "fraudScore.in=" + UPDATED_FRAUD_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByFraudScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fraudScore is not null
        defaultTransactionFiltering("fraudScore.specified=true", "fraudScore.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByFraudScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fraudScore is greater than or equal to
        defaultTransactionFiltering(
            "fraudScore.greaterThanOrEqual=" + DEFAULT_FRAUD_SCORE,
            "fraudScore.greaterThanOrEqual=" + UPDATED_FRAUD_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByFraudScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fraudScore is less than or equal to
        defaultTransactionFiltering(
            "fraudScore.lessThanOrEqual=" + DEFAULT_FRAUD_SCORE,
            "fraudScore.lessThanOrEqual=" + SMALLER_FRAUD_SCORE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByFraudScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fraudScore is less than
        defaultTransactionFiltering("fraudScore.lessThan=" + UPDATED_FRAUD_SCORE, "fraudScore.lessThan=" + DEFAULT_FRAUD_SCORE);
    }

    @Test
    @Transactional
    void getAllTransactionsByFraudScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where fraudScore is greater than
        defaultTransactionFiltering("fraudScore.greaterThan=" + SMALLER_FRAUD_SCORE, "fraudScore.greaterThan=" + DEFAULT_FRAUD_SCORE);
    }

    @Test
    @Transactional
    void getAllTransactionsByBlockedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where blocked equals to
        defaultTransactionFiltering("blocked.equals=" + DEFAULT_BLOCKED, "blocked.equals=" + UPDATED_BLOCKED);
    }

    @Test
    @Transactional
    void getAllTransactionsByBlockedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where blocked in
        defaultTransactionFiltering("blocked.in=" + DEFAULT_BLOCKED + "," + UPDATED_BLOCKED, "blocked.in=" + UPDATED_BLOCKED);
    }

    @Test
    @Transactional
    void getAllTransactionsByBlockedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where blocked is not null
        defaultTransactionFiltering("blocked.specified=true", "blocked.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reason equals to
        defaultTransactionFiltering("reason.equals=" + DEFAULT_REASON, "reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllTransactionsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reason in
        defaultTransactionFiltering("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON, "reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllTransactionsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reason is not null
        defaultTransactionFiltering("reason.specified=true", "reason.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reason contains
        defaultTransactionFiltering("reason.contains=" + DEFAULT_REASON, "reason.contains=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllTransactionsByReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reason does not contain
        defaultTransactionFiltering("reason.doesNotContain=" + UPDATED_REASON, "reason.doesNotContain=" + DEFAULT_REASON);
    }

    @Test
    @Transactional
    void getAllTransactionsByColorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where color equals to
        defaultTransactionFiltering("color.equals=" + DEFAULT_COLOR, "color.equals=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    void getAllTransactionsByColorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where color in
        defaultTransactionFiltering("color.in=" + DEFAULT_COLOR + "," + UPDATED_COLOR, "color.in=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    void getAllTransactionsByColorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where color is not null
        defaultTransactionFiltering("color.specified=true", "color.specified=false");
    }

    private void defaultTransactionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTransactionShouldBeFound(shouldBeFound);
        defaultTransactionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionShouldBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionReference").value(hasItem(DEFAULT_TRANSACTION_REFERENCE)))
            .andExpect(jsonPath("$.[*].fromAccount").value(hasItem(DEFAULT_FROM_ACCOUNT)))
            .andExpect(jsonPath("$.[*].toAccount").value(hasItem(DEFAULT_TO_ACCOUNT)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TRANSACTION_TYPE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].deviceId").value(hasItem(DEFAULT_DEVICE_ID)))
            .andExpect(jsonPath("$.[*].narration").value(hasItem(DEFAULT_NARRATION)))
            .andExpect(jsonPath("$.[*].fraudScore").value(hasItem(DEFAULT_FRAUD_SCORE)))
            .andExpect(jsonPath("$.[*].blocked").value(hasItem(DEFAULT_BLOCKED)))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR.toString())));

        // Check, that the count call also returns 1
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionShouldNotBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction
            .transactionReference(UPDATED_TRANSACTION_REFERENCE)
            .fromAccount(UPDATED_FROM_ACCOUNT)
            .toAccount(UPDATED_TO_ACCOUNT)
            .amount(UPDATED_AMOUNT)
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .status(UPDATED_STATUS)
            .location(UPDATED_LOCATION)
            .ipAddress(UPDATED_IP_ADDRESS)
            .createdAt(UPDATED_CREATED_AT)
            .deviceId(UPDATED_DEVICE_ID)
            .narration(UPDATED_NARRATION)
            .fraudScore(UPDATED_FRAUD_SCORE)
            .blocked(UPDATED_BLOCKED)
            .reason(UPDATED_REASON)
            .color(UPDATED_COLOR);
        TransactionDTO transactionDTO = transactionMapper.toDto(updatedTransaction);

        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTransactionToMatchAllProperties(updatedTransaction);
    }

    @Test
    @Transactional
    void putNonExistingTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .fromAccount(UPDATED_FROM_ACCOUNT)
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .deviceId(UPDATED_DEVICE_ID)
            .reason(UPDATED_REASON);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTransaction, transaction),
            getPersistedTransaction(transaction)
        );
    }

    @Test
    @Transactional
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .transactionReference(UPDATED_TRANSACTION_REFERENCE)
            .fromAccount(UPDATED_FROM_ACCOUNT)
            .toAccount(UPDATED_TO_ACCOUNT)
            .amount(UPDATED_AMOUNT)
            .transactionType(UPDATED_TRANSACTION_TYPE)
            .status(UPDATED_STATUS)
            .location(UPDATED_LOCATION)
            .ipAddress(UPDATED_IP_ADDRESS)
            .createdAt(UPDATED_CREATED_AT)
            .deviceId(UPDATED_DEVICE_ID)
            .narration(UPDATED_NARRATION)
            .fraudScore(UPDATED_FRAUD_SCORE)
            .blocked(UPDATED_BLOCKED)
            .reason(UPDATED_REASON)
            .color(UPDATED_COLOR);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionUpdatableFieldsEquals(partialUpdatedTransaction, getPersistedTransaction(partialUpdatedTransaction));
    }

    @Test
    @Transactional
    void patchNonExistingTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the transaction
        restTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, transaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return transactionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Transaction getPersistedTransaction(Transaction transaction) {
        return transactionRepository.findById(transaction.getId()).orElseThrow();
    }

    protected void assertPersistedTransactionToMatchAllProperties(Transaction expectedTransaction) {
        assertTransactionAllPropertiesEquals(expectedTransaction, getPersistedTransaction(expectedTransaction));
    }

    protected void assertPersistedTransactionToMatchUpdatableProperties(Transaction expectedTransaction) {
        assertTransactionAllUpdatablePropertiesEquals(expectedTransaction, getPersistedTransaction(expectedTransaction));
    }
}
