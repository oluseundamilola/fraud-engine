package com.app.fraudengine.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TransactionCriteriaTest {

    @Test
    void newTransactionCriteriaHasAllFiltersNullTest() {
        var transactionCriteria = new TransactionCriteria();
        assertThat(transactionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void transactionCriteriaFluentMethodsCreatesFiltersTest() {
        var transactionCriteria = new TransactionCriteria();

        setAllFilters(transactionCriteria);

        assertThat(transactionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void transactionCriteriaCopyCreatesNullFilterTest() {
        var transactionCriteria = new TransactionCriteria();
        var copy = transactionCriteria.copy();

        assertThat(transactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(transactionCriteria)
        );
    }

    @Test
    void transactionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var transactionCriteria = new TransactionCriteria();
        setAllFilters(transactionCriteria);

        var copy = transactionCriteria.copy();

        assertThat(transactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(transactionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var transactionCriteria = new TransactionCriteria();

        assertThat(transactionCriteria).hasToString("TransactionCriteria{}");
    }

    private static void setAllFilters(TransactionCriteria transactionCriteria) {
        transactionCriteria.id();
        transactionCriteria.transactionReference();
        transactionCriteria.fromAccount();
        transactionCriteria.toAccount();
        transactionCriteria.amount();
        transactionCriteria.transactionType();
        transactionCriteria.status();
        transactionCriteria.location();
        transactionCriteria.ipAddress();
        transactionCriteria.createdAt();
        transactionCriteria.deviceId();
        transactionCriteria.narration();
        transactionCriteria.fraudScore();
        transactionCriteria.blocked();
        transactionCriteria.reason();
        transactionCriteria.color();
        transactionCriteria.distinct();
    }

    private static Condition<TransactionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTransactionReference()) &&
                condition.apply(criteria.getFromAccount()) &&
                condition.apply(criteria.getToAccount()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getTransactionType()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getLocation()) &&
                condition.apply(criteria.getIpAddress()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getDeviceId()) &&
                condition.apply(criteria.getNarration()) &&
                condition.apply(criteria.getFraudScore()) &&
                condition.apply(criteria.getBlocked()) &&
                condition.apply(criteria.getReason()) &&
                condition.apply(criteria.getColor()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TransactionCriteria> copyFiltersAre(TransactionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTransactionReference(), copy.getTransactionReference()) &&
                condition.apply(criteria.getFromAccount(), copy.getFromAccount()) &&
                condition.apply(criteria.getToAccount(), copy.getToAccount()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getTransactionType(), copy.getTransactionType()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getLocation(), copy.getLocation()) &&
                condition.apply(criteria.getIpAddress(), copy.getIpAddress()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getDeviceId(), copy.getDeviceId()) &&
                condition.apply(criteria.getNarration(), copy.getNarration()) &&
                condition.apply(criteria.getFraudScore(), copy.getFraudScore()) &&
                condition.apply(criteria.getBlocked(), copy.getBlocked()) &&
                condition.apply(criteria.getReason(), copy.getReason()) &&
                condition.apply(criteria.getColor(), copy.getColor()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
