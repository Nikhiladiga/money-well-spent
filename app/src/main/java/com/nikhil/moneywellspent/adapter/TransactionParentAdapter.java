package com.nikhil.moneywellspent.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.moneywellspent.R;
import com.nikhil.moneywellspent.dto.TransactionSection;
import com.nikhil.moneywellspent.entity.Transaction;
import com.nikhil.moneywellspent.util.DateUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TransactionParentAdapter extends RecyclerView.Adapter<TransactionParentAdapter.TransactionViewHolder> {

    List<TransactionSection> sections;
    private Context context;
    private TransactionChildAdapter transactionChildAdapter;

    public TransactionParentAdapter(List<Transaction> transactions, Context context, ActivityResultLauncher<Intent> singleTransactionActivity) {
        this.context = context;
        this.sections = new ArrayList<>();

        LinkedHashMap<String, List<Transaction>> transactionConcurrentHashMap = new LinkedHashMap<>();
        for (Transaction transaction : transactions) {
            String currentTransactionDate = DateUtils.getInstance().convertTimestampToDate(transaction.getCreatedAt());
            //If an array is already present
            if (transactionConcurrentHashMap.containsKey(currentTransactionDate)) {
                Objects.requireNonNull(transactionConcurrentHashMap.get(currentTransactionDate)).add(transaction);
            } else {
                //If adding element for the first time
                List<Transaction> transactionsForDate = new ArrayList<>();
                transactionsForDate.add(transaction);
                transactionConcurrentHashMap.put(currentTransactionDate, transactionsForDate);
            }
        }

        for (Map.Entry<String, List<Transaction>> entry : transactionConcurrentHashMap.entrySet()) {
            TransactionSection transactionSection = new TransactionSection(entry.getKey(), entry.getValue());
            sections.add(transactionSection);
        }

    }


    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.transaction_section_row, parent, false);
        return new TransactionViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

        TransactionSection transactionSection = sections.get(position);

        String date = transactionSection.getDate();
        List<Transaction> items = transactionSection.getTransactions();
        double amount = items.stream().filter(transaction -> transaction.getIsCredit() == 0).mapToDouble(Transaction::getAmount).sum();

        holder.transactionHeaderDate.setText(date + " - " + DateUtils.getInstance().getDay(date));
        holder.transactionHeaderAmount.setText("₹" + amount);

        transactionChildAdapter = new TransactionChildAdapter(items, context);
        holder.transactionList.setAdapter(transactionChildAdapter);

    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<TransactionSection> transactionSections) {
        sections = transactionSections;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        private TextView transactionHeaderDate;
        private TextView transactionHeaderAmount;
        private RecyclerView transactionList;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionHeaderDate = itemView.findViewById(R.id.text_view_transaction_header_date);
            transactionHeaderAmount = itemView.findViewById(R.id.text_view_transaction_header_amount);
            transactionList = itemView.findViewById(R.id.recycler_view_transaction_list);
        }
    }

}
