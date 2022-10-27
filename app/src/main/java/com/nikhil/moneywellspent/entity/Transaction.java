package com.nikhil.moneywellspent.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.uuid.Generators;

@Entity(tableName = "transaction_table")
public class Transaction {

    @PrimaryKey()
    @NonNull
    private String id;
    private String name;
    private Integer isCredit;
    private Double amount;
    private String category;
    private Long createdAt;
    private String bank;
    private String emoji;
    private Integer isCustom;

    public Transaction() {
        id = Generators.timeBasedGenerator().generate().toString();
    }

    public Transaction(@NonNull String id, String name, Integer isCredit, Double amount, String category, Long createdAt, String bank, String emoji, Integer isCustom) {
        this.id = id;
        this.name = name;
        this.isCredit = isCredit;
        this.amount = amount;
        this.category = category;
        this.createdAt = createdAt;
        this.bank = bank;
        this.emoji = emoji;
        this.isCustom = isCustom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsCredit() {
        return isCredit;
    }

    public void setIsCredit(Integer isCredit) {
        this.isCredit = isCredit;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public Integer getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Integer isCustom) {
        this.isCustom = isCustom;
    }

    @NonNull
    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isCredit=" + isCredit +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                ", bank='" + bank + '\'' +
                ", emoji='" + emoji + '\'' +
                ", isCustom=" + isCustom +
                '}';
    }
}
