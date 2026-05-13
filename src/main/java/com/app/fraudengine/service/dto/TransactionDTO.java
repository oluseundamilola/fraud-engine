package com.app.fraudengine.service.dto;

import com.app.fraudengine.domain.enumeration.FraudColor;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.app.fraudengine.domain.Transaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private String transactionReference;

    @NotNull
    private String fromAccount;

    @NotNull
    private String toAccount;

    @NotNull
    private BigDecimal amount;

    private String transactionType;

    private String status;

    private String location;

    private String ipAddress;

    @NotNull
    private Instant createdAt;

    private String deviceId;

    private String narration;

    private Integer fraudScore;

    @NotNull
    private Boolean blocked;

    private String reason;

    private FraudColor color;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public Integer getFraudScore() {
        return fraudScore;
    }

    public void setFraudScore(Integer fraudScore) {
        this.fraudScore = fraudScore;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public FraudColor getColor() {
        return color;
    }

    public void setColor(FraudColor color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionDTO)) {
            return false;
        }

        TransactionDTO transactionDTO = (TransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionDTO{" +
            "id=" + getId() +
            ", transactionReference='" + getTransactionReference() + "'" +
            ", fromAccount='" + getFromAccount() + "'" +
            ", toAccount='" + getToAccount() + "'" +
            ", amount=" + getAmount() +
            ", transactionType='" + getTransactionType() + "'" +
            ", status='" + getStatus() + "'" +
            ", location='" + getLocation() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", deviceId='" + getDeviceId() + "'" +
            ", narration='" + getNarration() + "'" +
            ", fraudScore=" + getFraudScore() +
            ", blocked='" + getBlocked() + "'" +
            ", reason='" + getReason() + "'" +
            ", color='" + getColor() + "'" +
            "}";
    }
}
