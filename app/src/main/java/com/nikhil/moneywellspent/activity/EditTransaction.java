package com.nikhil.moneywellspent.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.nikhil.moneywellspent.MainActivity;
import com.nikhil.moneywellspent.R;
import com.nikhil.moneywellspent.databinding.ActivityEditTransactionBinding;
import com.nikhil.moneywellspent.entity.Transaction;
import com.nikhil.moneywellspent.util.DateUtils;
import com.nikhil.moneywellspent.util.SharedUtil;
import com.nikhil.moneywellspent.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EditTransaction extends AppCompatActivity {

    private ActivityEditTransactionBinding mBinding;
    private Transaction transaction;
    List<String> categories = new ArrayList<>();
    ArrayAdapter<String> categoryAdapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_transaction);
        intent = getIntent();
        transaction = new Transaction(
                intent.getStringExtra("id"),
                intent.getStringExtra("name"),
                intent.getIntExtra("isCredit", 0),
                intent.getDoubleExtra("amount", 0),
                intent.getStringExtra("category"),
                intent.getLongExtra("createdAt", 0),
                intent.getStringExtra("bankName"),
                intent.getStringExtra("emoji"),
                intent.getIntExtra("isCustom", 0)
        );

        mBinding.setTransaction(transaction);
        mBinding.setEdit(false);
        mBinding.setIsCredit(transaction.getIsCredit() == 1);

        handleEdit();
        handleCategoryList();
        handleDatePickerWidget();
        handleTransactionType();
        handleDeleteTransaction();
        updateTransaction();

    }

    private void handleCategoryList() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonString = sharedPreferences.getString("categories", null);
        if (jsonString != null) {
            try {
                categories.addAll(new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .readValue(jsonString, new TypeReference<List<String>>() {
                        }));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        categoryAdapter = new ArrayAdapter<>(this, R.layout.category_item, categories);
        mBinding.autocompleteTextViewCategory.setAdapter(categoryAdapter);
        mBinding.autocompleteTextViewCategory.setText(transaction.getCategory());
        categoryAdapter.getFilter().filter(null);
        mBinding.autocompleteTextViewCategory.setOnItemClickListener((adapterView, view, i, l) -> {
            System.out.println(mBinding.autocompleteTextViewCategory.getText().toString());
            if (mBinding.autocompleteTextViewCategory.getText().toString().equals("Custom")) {
                mBinding.autocompleteTextViewCategory.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                mBinding.autocompleteTextViewCategory.setInputType(InputType.TYPE_NULL);
            }
            categoryAdapter.getFilter().filter(null);
        });
    }

    private void handleEdit() {
        mBinding.imageViewEdit.setOnClickListener(view -> mBinding.setEdit(!mBinding.getEdit()));
    }

    private void handleTransactionType() {
        mBinding.textViewDebit.setOnClickListener(view -> {
            mBinding.setIsCredit(false);
        });

        mBinding.textViewCredit.setOnClickListener(view -> {
            mBinding.setIsCredit(true);
        });

    }

    private void handleDatePickerWidget() {
        TextInputEditText date = mBinding.textInputEditTextDate;

        //Fix to hide keyboard
        date.setInputType(InputType.TYPE_NULL);
        date.setKeyListener(null);

        date.setOnFocusChangeListener((v, b) -> {
            if (b) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                        .datePicker()
                        .setTitleText("Transaction date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    Date date1 = new Date(selection);
                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    date.setText(df.format(date1));
                    date.clearFocus();
                });
            }
        });
    }

    private void handleDeleteTransaction() {

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Delete transaction
                    MainActivity.getInstance().transactionViewModel.deleteTransaction(transaction);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("success", true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };
        mBinding.imageViewDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void updateTransaction() {
        mBinding.updateTransaction.setOnClickListener(view -> {
            Long createdAt;
            Long currentTransactionTs = intent.getLongExtra("createdAt", 0);

            if (Objects.requireNonNull(mBinding.textInputEditTextDate.getText()).toString().equalsIgnoreCase(DateUtils.getInstance().convertTimestampToDate(currentTransactionTs))) {
                createdAt = currentTransactionTs;
            } else {
                createdAt = Objects.requireNonNull(DateUtils.getInstance().convertStringToTimestamp(String.valueOf(mBinding.textInputEditTextDate.getText()))).getTime();
            }

            Transaction transaction = new Transaction(
                    intent.getStringExtra("id"),
                    String.valueOf(mBinding.textInputEditTextPayeeName.getText()),
                    mBinding.getIsCredit() ? 1 : 0,
                    StringUtils.getInstance().convertStringAmountToDouble(Objects.requireNonNull(mBinding.textInputEditTextAmount.getText()).toString()),
                    String.valueOf(mBinding.autocompleteTextViewCategory.getText()),
                    createdAt,
                    String.valueOf(mBinding.textInputEditTextBankName.getText()),
                    String.valueOf(mBinding.textInputEditTextEmoji.getText()),
                    mBinding.customExpense.isChecked() ? 1 : 0
            );

            mBinding.setEdit(false);
            MainActivity.getInstance().transactionViewModel.updateTransaction(transaction);
            SharedUtil.getInstance().saveCategory(transaction.getCategory());
            Toast.makeText(this, "Transaction details updated", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}