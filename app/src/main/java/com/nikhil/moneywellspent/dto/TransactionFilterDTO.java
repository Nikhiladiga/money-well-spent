package com.nikhil.moneywellspent.dto;

import androidx.annotation.NonNull;


import java.util.Objects;

public class TransactionFilterDTO {
    private String currentMonth;
    private Integer currentYear;

    public TransactionFilterDTO() {
    }

    public TransactionFilterDTO(String currentMonth, Integer currentYear) {
        this.currentMonth = currentMonth;
        this.currentYear = currentYear;
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(Integer currentYear) {
        this.currentYear = currentYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionFilterDTO that = (TransactionFilterDTO) o;
        return Objects.equals(currentMonth, that.currentMonth) && Objects.equals(currentYear, that.currentYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentMonth, currentYear);
    }

    @NonNull
    @Override
    public String toString() {
        return "TransactionFilterDTO{" +
                "currentMonth='" + currentMonth + '\'' +
                ", currentYear='" + currentYear + '\'' +
                '}';
    }
}
