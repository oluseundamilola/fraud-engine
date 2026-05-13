package com.app.fraudengine.domain;

import com.app.fraudengine.domain.enumeration.FraudColor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "transaction_reference", nullable = false, unique = true)
    private String transactionReference;

    @NotNull
    @Column(name = "from_account", nullable = false)
    private String fromAccount;

    @NotNull
    @Column(name = "to_account", nullable = false)
    private String toAccount;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "status")
    private String status;

    @Column(name = "location")
    private String location;

    @Column(name = "ip_address")
    private String ipAddress;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "narration")
    private String narration;

    @Column(name = "fraud_score")
    private Integer fraudScore;

    @NotNull
    @Column(name = "blocked", nullable = false)
    private Boolean blocked;

    @Column(name = "reason")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private FraudColor color;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionReference() {
        return this.transactionReference;
    }

    public Transaction transactionReference(String transactionReference) {
        this.setTransactionReference(transactionReference);
        return this;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getFromAccount() {
        return this.fromAccount;
    }

    public Transaction fromAccount(String fromAccount) {
        this.setFromAccount(fromAccount);
        return this;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return this.toAccount;
    }

    public Transaction toAccount(String toAccount) {
        this.setToAccount(toAccount);
        return this;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Transaction amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public Transaction transactionType(String transactionType) {
        this.setTransactionType(transactionType);
        return this;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStatus() {
        return this.status;
    }

    public Transaction status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return this.location;
    }

    public Transaction location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public Transaction ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Transaction createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public Transaction deviceId(String deviceId) {
        this.setDeviceId(deviceId);
        return this;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNarration() {
        return this.narration;
    }

    public Transaction narration(String narration) {
        this.setNarration(narration);
        return this;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public Integer getFraudScore() {
        return this.fraudScore;
    }

    public Transaction fraudScore(Integer fraudScore) {
        this.setFraudScore(fraudScore);
        return this;
    }

    public void setFraudScore(Integer fraudScore) {
        this.fraudScore = fraudScore;
    }

    public Boolean getBlocked() {
        return this.blocked;
    }

    public Transaction blocked(Boolean blocked) {
        this.setBlocked(blocked);
        return this;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public String getReason() {
        return this.reason;
    }

    public Transaction reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public FraudColor getColor() {
        return this.color;
    }

    public Transaction color(FraudColor color) {
        this.setColor(color);
        return this;
    }

    public void setColor(FraudColor color) {
        this.color = color;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return getId() != null && getId().equals(((Transaction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
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
