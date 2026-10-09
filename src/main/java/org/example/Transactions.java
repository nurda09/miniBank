package org.example;

import java.time.LocalDateTime;

public class Transactions {
    private Integer id;
    private Integer fromAccountId; // null если это пополнение -> toAccount получит допустим бабки, но отправителя нету
    private Integer toAccountId; // null если это снятие -> toAccountid не получит нихуя, поэтому это будет снятие
    private double amount;
    private Type type;
    private LocalDateTime createdAt;

    public enum Type{
        Deposit, Withdrawal, Transfer
    };

    public Transactions(Integer id, Integer fromAccountId, Integer toAccountId, double amount, Type type){
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
    }

    public Transactions(Integer fromAccountId, Integer toAccountId, double amount, Type type){
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Integer getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Integer toAccountId) {
        this.toAccountId = toAccountId;
    }

    public Integer getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Integer fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Transactions{" +
                "id=" + id +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                ", type=" + type +
                ", createdAt=" + createdAt +
                '}';
    }
}
