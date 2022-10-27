package com.nikhil.moneywellspent.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.moneywellspent.MainActivity;
import com.nikhil.moneywellspent.R;
import com.nikhil.moneywellspent.activity.EditTransaction;
import com.nikhil.moneywellspent.entity.Transaction;
import com.nikhil.moneywellspent.util.DateUtils;

import java.util.List;

public class TransactionChildAdapter extends RecyclerView.Adapter<TransactionChildAdapter.TransactionViewHolder> {

    List<Transaction> transactions;
    private Context context;

    public TransactionChildAdapter(List<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.transaction_list_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        if ((transaction.getIsCredit() == 1)) {
            holder.transactionAmount.setTextColor(Color.GREEN);
            holder.transactionAmount.setText("+ ₹" + transaction.getAmount().toString());
        } else {
            holder.transactionAmount.setTextColor(Color.RED);
            holder.transactionAmount.setText("- ₹" + transaction.getAmount().toString());
        }

        holder.categoryEmoji.setText(transaction.getEmoji());

        if (transaction.getIsCustom() == 1) {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = MainActivity.getInstance().getResources().getDrawable(R.drawable.circle);
            holder.transactionName.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }

        holder.transactionName.setText(transaction.getName());

        holder.transactionCategory.setText(transaction.getCategory());
        holder.transactionDate.setText(DateUtils.getInstance().convertTimestampToDate(transaction.getCreatedAt()));
        holder.transactionBank.setText(transaction.getBank());

        holder.parentView.setClickable(true);
        holder.parentView.setOnClickListener(view -> {
            Intent intent = new Intent(this.context, EditTransaction.class);
            intent.putExtra("id", transaction.getId());
            intent.putExtra("isCredit", transaction.getIsCredit());
            intent.putExtra("name", transaction.getName());
            intent.putExtra("amount", transaction.getAmount());
            intent.putExtra("category", transaction.getCategory());
            intent.putExtra("createdAt", transaction.getCreatedAt());
            intent.putExtra("bankName", transaction.getBank());
            intent.putExtra("emoji", transaction.getEmoji());
            intent.putExtra("isCustom", transaction.getIsCustom());
            MainActivity.getInstance().startActivity(intent);
            MainActivity.getInstance().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryEmoji;
        private TextView transactionName;
        private TextView transactionCategory;
        private TextView transactionAmount;
        private TextView transactionDate;
        private TextView transactionBank;
        private View parentView;
        private CheckBox isSelected;

        TransactionViewHolder(@NonNull View view) {
            super(view);
            this.parentView = view;
            categoryEmoji = view.findViewById(R.id.text_view_emoji);
            transactionName = view.findViewById(R.id.text_view_payee);
            transactionCategory = view.findViewById(R.id.text_view_category);
            transactionAmount = view.findViewById(R.id.text_view_amount);
            transactionDate = view.findViewById(R.id.text_view_date);
            transactionBank = view.findViewById(R.id.text_view_bank);
        }


    }
}
