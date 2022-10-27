package com.nikhil.moneywellspent.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nikhil.moneywellspent.dao.TransactionDao;
import com.nikhil.moneywellspent.entity.Transaction;

@Database(entities = {Transaction.class}, version = 2)
public abstract class TransactionDatabase extends RoomDatabase {
    private static TransactionDatabase instance;

    public abstract TransactionDao transactionDao();

    public static synchronized TransactionDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room
                    .databaseBuilder(context.getApplicationContext(), TransactionDatabase.class, "transaction_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            System.out.println("INSIDE THIS FUNCTION!");
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private TransactionDao transactionDao;

        public PopulateDBAsyncTask(TransactionDatabase transactionDatabase) {
            this.transactionDao = transactionDatabase.transactionDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Insert transactions here
            transactionDao.insertTransaction(new Transaction("1", "Nikhil Adiga", 0, 233.22, "Food", 1666850537956L, "Axis Bank", "🥞", 1));
            transactionDao.insertTransaction(new Transaction("2", "Nikhil Adiga", 1, 333.22, "Bike", 1666850537956L, "Axis Bank", "🥞", 1));
            transactionDao.insertTransaction(new Transaction("3", "Nikhil Adiga", 0, 433.22, "Recharge", 1666850537956L, "Axis Bank", "🥞", 1));
            transactionDao.insertTransaction(new Transaction("4", "Nikhil Adiga", 1, 533.22, "Investment", 1666850537956L, "Axis Bank", "🥞", 1));
            return null;
        }
    }

}
