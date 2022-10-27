package com.nikhil.moneywellspent.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nikhil.moneywellspent.MainActivity;
import com.nikhil.moneywellspent.R;
import com.nikhil.moneywellspent.databinding.ActivityAddTransactionBinding;
import com.nikhil.moneywellspent.entity.Transaction;
import com.nikhil.moneywellspent.util.DateUtils;
import com.nikhil.moneywellspent.util.SharedUtil;
import com.nikhil.moneywellspent.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class AddTransaction extends AppCompatActivity {

    private ActivityAddTransactionBinding mBinding;
    private ArrayAdapter<String> categoryAdapter;
    private List<String> categories;
    private Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_transaction);

        //Create transacton object
        transaction = new Transaction();
        transaction.setIsCredit(0);
        transaction.setIsCustom(1);

        mBinding.setTransaction(transaction);
        mBinding.setIsCredit(false);

        handleCategoryList();
        handleDatePickerWidget();
        handleTransactionType();
        handleAddTransaction();
    }

    private void handleCategoryList() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonString = sharedPreferences.getString("categories", null);
        if (jsonString != null) {
            try {
                categories = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .readValue(jsonString, new TypeReference<List<String>>() {
                        });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        categoryAdapter = new ArrayAdapter<>(this, R.layout.category_item, categories);
        mBinding.autocompleteTextViewCategory.setAdapter(categoryAdapter);
        mBinding.autocompleteTextViewCategory.setOnItemClickListener((adapterView, view, i, l) -> {
            if (mBinding.autocompleteTextViewCategory.getText().toString().equals("Custom")) {
                mBinding.autocompleteTextViewCategory.setText("");
                mBinding.autocompleteTextViewCategory.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                mBinding.autocompleteTextViewCategory.setInputType(InputType.TYPE_NULL);
            }
            categoryAdapter.getFilter().filter(null);
        });
    }

    private void handleTransactionType() {
        mBinding.textViewDebit.setOnClickListener(view -> {
            mBinding.setIsCredit(false);
            transaction.setIsCredit(0);
        });

        mBinding.textViewCredit.setOnClickListener(view -> {
            mBinding.setIsCredit(true);
            transaction.setIsCredit(1);
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
                    java.util.Date date1 = new java.util.Date(selection);
                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    date.setText(df.format(date1));
                    date.clearFocus();
                });
            }
        });
    }

    private void handleAddTransaction() {
        mBinding.buttonAddTransaction.setOnClickListener(v -> {

            if (validateTransaction()) {

                //Set id
                transaction.setId(Generators.timeBasedGenerator().generate().toString());

                //TODO - learn two way binding and change this
                transaction.setAmount(StringUtils.instance.convertStringAmountToDouble(mBinding.textInputEditTextAmount.getText().toString()));
                transaction.setCreatedAt(DateUtils.instance.convertStringToTimestamp(mBinding.textInputEditTextDate.getText().toString()).getTime());

                //Trim values
                transaction.setName(transaction.getName().trim());
                transaction.setBank(transaction.getBank().trim());
                transaction.setEmoji(transaction.getEmoji().trim());

                if (mBinding.customExpense.isChecked()) {
                    transaction.setIsCustom(1);
                } else {
                    transaction.setIsCustom(0);
                }

                //Save transaction to database
                MainActivity.getInstance().transactionViewModel.insertTransaction(transaction);
                SharedUtil.getInstance().saveCategory(transaction.getCategory());
                Toast.makeText(this, "Transaction added!", Toast.LENGTH_SHORT).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("success", "true");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private boolean validateTransaction() {
        //Check if date is null
        Editable dateText = mBinding.textInputEditTextDate.getText();
        if (dateText != null && dateText.length() < 1) {
            TextInputLayout dateLayout = mBinding.textInputLayoutDate;
            dateLayout.setError("Please enter a valid date");
            return false;
        }

        //Check if payee name is null
        Editable payeeName = mBinding.textInputEditTextPayeeName.getText();
        if (payeeName != null && payeeName.length() < 1) {
            TextInputLayout payeeNameLayout = mBinding.textInputLayoutPayeeName;
            payeeNameLayout.setError("Please enter a valid payee name");
            return false;
        }

        //Check if amount is null
        Editable amountPaid = mBinding.textInputEditTextAmount.getText();
        if (amountPaid == null || String.valueOf(amountPaid).equals("0") || amountPaid.length() < 1) {
            TextInputLayout amountPaidLayout = mBinding.textInputLayoutAmount;
            amountPaidLayout.setError("Please enter a valid amount");
            return false;
        }

        //Check if category is null
        Editable category = mBinding.autocompleteTextViewCategory.getText();
        if (category == null || category.length() < 1) {
            TextInputLayout categoryLayout = mBinding.textInputLayoutCategory;
            categoryLayout.setError("Please select a category");
            return false;
        }

        //Check if bank is null
        Editable bankName = mBinding.textInputEditTextBankName.getText();
        if (bankName == null || bankName.length() < 1) {
            mBinding.textInputLayoutBankName.setError("Please enter bank name");
            return false;
        }

        //Check if emoji is null
        Editable emoji = mBinding.textInputEditTextEmoji.getText();
        if (emoji == null || emoji.length() < 1) {
            mBinding.textInputLayoutEmoji.setError("Please enter an emoji");
            return false;
        }

        return true;
    }

}