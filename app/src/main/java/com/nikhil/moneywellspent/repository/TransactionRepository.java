package com.nikhil.moneywellspent.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.nikhil.moneywellspent.dao.TransactionDao;
import com.nikhil.moneywellspent.database.TransactionDatabase;
import com.nikhil.moneywellspent.entity.Transaction;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TransactionRepository {

    private TransactionDao transactionDao;

    public TransactionRepository(Application application) {
        TransactionDatabase transactionDatabase = TransactionDatabase.getInstance(application.getApplicationContext());
        transactionDao = transactionDatabase.transactionDao();
    }

    public void insertTransaction(Transaction transaction) {
        new InsertTransactionAsyncTask(transactionDao).execute(transaction);
    }

    public void updateTransaction(Transaction transaction) {
        new UpdateTransactionAsyncTask(transactionDao).execute(transaction);
    }

    public void deleteTransaction(Transaction transaction) {
        new DeleteTransactionAsyncTask(transactionDao).execute(transaction);
    }

    public void deleteAllTransactions() {
        new DeleteAllTransactionsAsyncTask(transactionDao).execute();
    }

    public LiveData<List<Transaction>> getTransactionsByTimeframe(Long startDate, Long endDate) {
        return transactionDao.getTransactionsByTimeframe(startDate, endDate);
    }

    public Long getLastTransactionDate() {
        try {
            return new LastTransactionDateAsyncTask(transactionDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class InsertTransactionAsyncTask extends AsyncTask<Transaction, Void, Void> {

        private TransactionDao transactionDao;

        public InsertTransactionAsyncTask(TransactionDao transactionDao) {
            this.transactionDao = transactionDao;
        }

        @Override
        protected Void doInBackground(Transaction... transactions) {
            this.transactionDao.insertTransaction(transactions[0]);
            return null;
        }
    }

    private static class UpdateTransactionAsyncTask extends AsyncTask<Transaction, Void, Void> {

        private TransactionDao transactionDao;

        public UpdateTransactionAsyncTask(TransactionDao transactionDao) {
            this.transactionDao = transactionDao;
        }

        @Override
        protected Void doInBackground(Transaction... transactions) {
            this.transactionDao.updateTransaction(transactions[0]);
            return null;
        }
    }

    private static class DeleteTransactionAsyncTask extends AsyncTask<Transaction, Void, Void> {

        private TransactionDao transactionDao;

        public DeleteTransactionAsyncTask(TransactionDao transactionDao) {
            this.transactionDao = transactionDao;
        }

        @Override
        protected Void doInBackground(Transaction... transactions) {
            this.transactionDao.deleteTransaction(transactions[0]);
            return null;
        }
    }

    private static class DeleteAllTransactionsAsyncTask extends AsyncTask<Void, Void, Void> {

        private TransactionDao transactionDao;

        public DeleteAllTransactionsAsyncTask(TransactionDao transactionDao) {
            this.transactionDao = transactionDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.transactionDao.deleteAllTransactions();
            return null;
        }
    }

    private static class LastTransactionDateAsyncTask extends AsyncTask<Void, Void, Long> {

        private TransactionDao transactionDao;

        public LastTransactionDateAsyncTask(TransactionDao transactionDao) {
            this.transactionDao = transactionDao;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return this.transactionDao.getLastTransactionDate();
        }
    }


}
