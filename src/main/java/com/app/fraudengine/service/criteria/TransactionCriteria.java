package com.app.fraudengine.service.criteria;

import com.app.fraudengine.domain.enumeration.FraudColor;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.app.fraudengine.domain.Transaction} entity. This class is used
 * in {@link com.app.fraudengine.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering FraudColor
     */
    public static class FraudColorFilter extends Filter<FraudColor> {

        public FraudColorFilter() {}

        public FraudColorFilter(FraudColorFilter filter) {
            super(filter);
        }

        @Override
        public FraudColorFilter copy() {
            return new FraudColorFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter transactionReference;

    private StringFilter fromAccount;

    private StringFilter toAccount;

    private BigDecimalFilter amount;

    private StringFilter transactionType;

    private StringFilter status;

    private StringFilter location;

    private StringFilter ipAddress;

    private InstantFilter createdAt;

    private StringFilter deviceId;

    private StringFilter narration;

    private IntegerFilter fraudScore;

    private BooleanFilter blocked;

    private StringFilter reason;

    private FraudColorFilter color;

    private Boolean distinct;

    public TransactionCriteria() {}

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.transactionReference = other.optionalTransactionReference().map(StringFilter::copy).orElse(null);
        this.fromAccount = other.optionalFromAccount().map(StringFilter::copy).orElse(null);
        this.toAccount = other.optionalToAccount().map(StringFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.transactionType = other.optionalTransactionType().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.location = other.optionalLocation().map(StringFilter::copy).orElse(null);
        this.ipAddress = other.optionalIpAddress().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.deviceId = other.optionalDeviceId().map(StringFilter::copy).orElse(null);
        this.narration = other.optionalNarration().map(StringFilter::copy).orElse(null);
        this.fraudScore = other.optionalFraudScore().map(IntegerFilter::copy).orElse(null);
        this.blocked = other.optionalBlocked().map(BooleanFilter::copy).orElse(null);
        this.reason = other.optionalReason().map(StringFilter::copy).orElse(null);
        this.color = other.optionalColor().map(FraudColorFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TransactionCriteria copy() {
        return new TransactionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTransactionReference() {
        return transactionReference;
    }

    public Optional<StringFilter> optionalTransactionReference() {
        return Optional.ofNullable(transactionReference);
    }

    public StringFilter transactionReference() {
        if (transactionReference == null) {
            setTransactionReference(new StringFilter());
        }
        return transactionReference;
    }

    public void setTransactionReference(StringFilter transactionReference) {
        this.transactionReference = transactionReference;
    }

    public StringFilter getFromAccount() {
        return fromAccount;
    }

    public Optional<StringFilter> optionalFromAccount() {
        return Optional.ofNullable(fromAccount);
    }

    public StringFilter fromAccount() {
        if (fromAccount == null) {
            setFromAccount(new StringFilter());
        }
        return fromAccount;
    }

    public void setFromAccount(StringFilter fromAccount) {
        this.fromAccount = fromAccount;
    }

    public StringFilter getToAccount() {
        return toAccount;
    }

    public Optional<StringFilter> optionalToAccount() {
        return Optional.ofNullable(toAccount);
    }

    public StringFilter toAccount() {
        if (toAccount == null) {
            setToAccount(new StringFilter());
        }
        return toAccount;
    }

    public void setToAccount(StringFilter toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public Optional<BigDecimalFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            setAmount(new BigDecimalFilter());
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
    }

    public StringFilter getTransactionType() {
        return transactionType;
    }

    public Optional<StringFilter> optionalTransactionType() {
        return Optional.ofNullable(transactionType);
    }

    public StringFilter transactionType() {
        if (transactionType == null) {
            setTransactionType(new StringFilter());
        }
        return transactionType;
    }

    public void setTransactionType(StringFilter transactionType) {
        this.transactionType = transactionType;
    }

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public StringFilter getLocation() {
        return location;
    }

    public Optional<StringFilter> optionalLocation() {
        return Optional.ofNullable(location);
    }

    public StringFilter location() {
        if (location == null) {
            setLocation(new StringFilter());
        }
        return location;
    }

    public void setLocation(StringFilter location) {
        this.location = location;
    }

    public StringFilter getIpAddress() {
        return ipAddress;
    }

    public Optional<StringFilter> optionalIpAddress() {
        return Optional.ofNullable(ipAddress);
    }

    public StringFilter ipAddress() {
        if (ipAddress == null) {
            setIpAddress(new StringFilter());
        }
        return ipAddress;
    }

    public void setIpAddress(StringFilter ipAddress) {
        this.ipAddress = ipAddress;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public StringFilter getDeviceId() {
        return deviceId;
    }

    public Optional<StringFilter> optionalDeviceId() {
        return Optional.ofNullable(deviceId);
    }

    public StringFilter deviceId() {
        if (deviceId == null) {
            setDeviceId(new StringFilter());
        }
        return deviceId;
    }

    public void setDeviceId(StringFilter deviceId) {
        this.deviceId = deviceId;
    }

    public StringFilter getNarration() {
        return narration;
    }

    public Optional<StringFilter> optionalNarration() {
        return Optional.ofNullable(narration);
    }

    public StringFilter narration() {
        if (narration == null) {
            setNarration(new StringFilter());
        }
        return narration;
    }

    public void setNarration(StringFilter narration) {
        this.narration = narration;
    }

    public IntegerFilter getFraudScore() {
        return fraudScore;
    }

    public Optional<IntegerFilter> optionalFraudScore() {
        return Optional.ofNullable(fraudScore);
    }

    public IntegerFilter fraudScore() {
        if (fraudScore == null) {
            setFraudScore(new IntegerFilter());
        }
        return fraudScore;
    }

    public void setFraudScore(IntegerFilter fraudScore) {
        this.fraudScore = fraudScore;
    }

    public BooleanFilter getBlocked() {
        return blocked;
    }

    public Optional<BooleanFilter> optionalBlocked() {
        return Optional.ofNullable(blocked);
    }

    public BooleanFilter blocked() {
        if (blocked == null) {
            setBlocked(new BooleanFilter());
        }
        return blocked;
    }

    public void setBlocked(BooleanFilter blocked) {
        this.blocked = blocked;
    }

    public StringFilter getReason() {
        return reason;
    }

    public Optional<StringFilter> optionalReason() {
        return Optional.ofNullable(reason);
    }

    public StringFilter reason() {
        if (reason == null) {
            setReason(new StringFilter());
        }
        return reason;
    }

    public void setReason(StringFilter reason) {
        this.reason = reason;
    }

    public FraudColorFilter getColor() {
        return color;
    }

    public Optional<FraudColorFilter> optionalColor() {
        return Optional.ofNullable(color);
    }

    public FraudColorFilter color() {
        if (color == null) {
            setColor(new FraudColorFilter());
        }
        return color;
    }

    public void setColor(FraudColorFilter color) {
        this.color = color;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionCriteria that = (TransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(transactionReference, that.transactionReference) &&
            Objects.equals(fromAccount, that.fromAccount) &&
            Objects.equals(toAccount, that.toAccount) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(transactionType, that.transactionType) &&
            Objects.equals(status, that.status) &&
            Objects.equals(location, that.location) &&
            Objects.equals(ipAddress, that.ipAddress) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(deviceId, that.deviceId) &&
            Objects.equals(narration, that.narration) &&
            Objects.equals(fraudScore, that.fraudScore) &&
            Objects.equals(blocked, that.blocked) &&
            Objects.equals(reason, that.reason) &&
            Objects.equals(color, that.color) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            transactionReference,
            fromAccount,
            toAccount,
            amount,
            transactionType,
            status,
            location,
            ipAddress,
            createdAt,
            deviceId,
            narration,
            fraudScore,
            blocked,
            reason,
            color,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTransactionReference().map(f -> "transactionReference=" + f + ", ").orElse("") +
            optionalFromAccount().map(f -> "fromAccount=" + f + ", ").orElse("") +
            optionalToAccount().map(f -> "toAccount=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalTransactionType().map(f -> "transactionType=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalLocation().map(f -> "location=" + f + ", ").orElse("") +
            optionalIpAddress().map(f -> "ipAddress=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalDeviceId().map(f -> "deviceId=" + f + ", ").orElse("") +
            optionalNarration().map(f -> "narration=" + f + ", ").orElse("") +
            optionalFraudScore().map(f -> "fraudScore=" + f + ", ").orElse("") +
            optionalBlocked().map(f -> "blocked=" + f + ", ").orElse("") +
            optionalReason().map(f -> "reason=" + f + ", ").orElse("") +
            optionalColor().map(f -> "color=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
