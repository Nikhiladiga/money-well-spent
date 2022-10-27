package com.nikhil.moneywellspent.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.nikhil.moneywellspent.dto.TransactionFilterDTO;
import com.nikhil.moneywellspent.entity.Transaction;
import com.nikhil.moneywellspent.repository.TransactionRepository;
import com.nikhil.moneywellspent.util.DateUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionViewModel extends AndroidViewModel {

    private TransactionRepository transactionRepository;
    public MutableLiveData<TransactionFilterDTO> transactionFilterDTOMutableLiveData = new MutableLiveData<>();

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
    }

    public void setTransactionFilterDTOMutableLiveData(TransactionFilterDTO transactionFilterDTO) {
        transactionFilterDTOMutableLiveData.setValue(transactionFilterDTO);
    }

    public LiveData<List<Transaction>> liveTransactionsList = Transformations.switchMap(transactionFilterDTOMutableLiveData, input -> {
        ConcurrentHashMap<String, Long> timeframe = DateUtils.getInstance().getMonthStartAndMonthEndTimestamp(input.getCurrentYear(), input.getCurrentMonth());
        return this.transactionRepository.getTransactionsByTimeframe(timeframe.get("start"), timeframe.get("end"));
    });

    public void insertTransaction(Transaction transaction) {
        transactionRepository.insertTransaction(transaction);
    }

    public void updateTransaction(Transaction transaction) {
        transactionRepository.updateTransaction(transaction);
    }

    public void deleteTransaction(Transaction transaction) {
        transactionRepository.deleteTransaction(transaction);
    }

    public void deleteAllTransactions() {
        transactionRepository.deleteAllTransactions();
    }

    public Long getLastTransactionDate() {
        return transactionRepository.getLastTransactionDate();
    }
}
