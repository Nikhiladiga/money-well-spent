package com.nikhil.moneywellspent.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nikhil.moneywellspent.entity.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insertTransaction(Transaction transaction);

    @Update
    void updateTransaction(Transaction transaction);

    @Delete
    void deleteTransaction(Transaction transaction);

    @Query("SELECT * FROM transaction_table WHERE createdAt >=:monthStart AND createdAt<=:monthEnd ORDER By createdAt DESC")
    LiveData<List<Transaction>> getTransactionsByTimeframe(Long monthStart, Long monthEnd);

    @Query("DELETE FROM transaction_table")
    void deleteAllTransactions();

    @Query("SELECT createdAt FROM transaction_table ORDER BY createdAt DESC LIMIT 1")
    Long getLastTransactionDate();
}
