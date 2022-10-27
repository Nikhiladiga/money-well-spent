package com.nikhil.moneywellspent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.nikhil.moneywellspent.activity.AddTransaction;
import com.nikhil.moneywellspent.adapter.TransactionParentAdapter;
import com.nikhil.moneywellspent.databinding.ActivityMainBinding;
import com.nikhil.moneywellspent.dto.TransactionFilterDTO;
import com.nikhil.moneywellspent.util.DateUtils;
import com.nikhil.moneywellspent.util.SharedPrefHelper;
import com.nikhil.moneywellspent.util.TopSheetBehavior;
import com.nikhil.moneywellspent.viewmodel.TransactionViewModel;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    //View model
    public TransactionViewModel transactionViewModel;

    //Data-binding variables
    private ActivityMainBinding mBinding;
    private String greeting;
    private boolean isEmpty;
    private double balance;
    private double expense;
    private String currentMonth;

    //Search and Filter bottom sheets
    private TopSheetBehavior searchSheet;
    private TopSheetBehavior filterSheet;

    private static WeakReference<MainActivity> mainActivityWeakReference;

    //Filter data
    private int currentYear;
    private String currentCategory;
    private TransactionFilterDTO transactionFilterDTO = new TransactionFilterDTO();

    //Adapter variables
    TransactionParentAdapter transactionParentAdapter;

    //Activities
    private ActivityResultLauncher<Intent> addTransactionActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Data binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mainActivityWeakReference = new WeakReference<>(MainActivity.this);

        //Create shared prefs helper class
        SharedPrefHelper.initSharedPrefHelper(PreferenceManager.getDefaultSharedPreferences(this));

        //Handle greeting
        handleGreeting();

        //Handle search and filter top sheets
        handleSearchFilterTopSheets();

        //Set menu items and init bottom app bar
        setSupportActionBar(mBinding.bottomAppBar);
        getMenuInflater().inflate(R.menu.left_menu, mBinding.menuViewLeft.getMenu());

        //Set current year,month and category
        currentYear = DateUtils.getInstance().getCurrentYear();
        currentMonth = DateUtils.getInstance().getCurrentMonth();
        currentCategory = null;

        //Set data binding variables
        mBinding.setMonth(currentMonth);

        //Set filter variables
        transactionFilterDTO.setCurrentMonth(currentMonth);
        transactionFilterDTO.setCurrentYear(currentYear);

        //Check if sms permissions are provided
        checkSmsPermissions();

        //Set view model
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transactionViewModel.setTransactionFilterDTOMutableLiveData(transactionFilterDTO);
        transactionViewModel.liveTransactionsList.observe(this, transactions -> {
            System.out.println("TRANSACTIONS:" + transactions);

            mBinding.setIsEmpty(transactions.size() == 0);

            for (int i = 0; i < transactions.size(); i++) {
                if (transactions.get(i).getIsCredit() == 0) {
                    expense += transactions.get(i).getAmount();
                    balance -= transactions.get(i).getAmount();
                } else {
                    balance += transactions.get(i).getAmount();
                }
            }

            mBinding.setBalance(balance);
            mBinding.setExpense(expense);

            transactionParentAdapter = new TransactionParentAdapter(transactions, getBaseContext(), null);
            mBinding.recylerViewTransactionSection.setAdapter(transactionParentAdapter);
            mBinding.recylerViewTransactionSection.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recylerViewTransactionSection.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        });

        //Open add transaction activity
        handleAddTransaction();

    }

    private void handleGreeting() {
        String username = SharedPrefHelper.getUsername();
        if (username != null) {
            mBinding.setGreeting("Hello " + username);
        } else {
            mBinding.setGreeting("Hello");
        }
    }

    private void handleAddTransaction() {
        mBinding.addTransactionBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddTransaction.class);
            startActivity(intent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean isPermissionGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = false;
                    break;
                }
            }
            if (isPermissionGranted) {
//                Util.readAllSms(currentMonth);
            } else {
                Toast.makeText(this, "Please give required permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkSmsPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS
            }, 1);
        } else {
//            Util.readAllSms(currentMonth);
        }
    }

    private void handleSearchFilterTopSheets() {
        searchSheet = TopSheetBehavior.from(mBinding.layoutTopSheetSearch);
        searchSheet.setPeekHeight(300);
        searchSheet.setHideable(true);
        searchSheet.setState(TopSheetBehavior.STATE_HIDDEN);

        mBinding.imageViewCloseSearch.setOnClickListener(view -> searchSheet.setState(TopSheetBehavior.STATE_HIDDEN));
        mBinding.imageViewSearch.setOnClickListener(view -> searchSheet.setState(TopSheetBehavior.STATE_EXPANDED));

        filterSheet = TopSheetBehavior.from(mBinding.layoutTopSheetFilter);
        filterSheet.setPeekHeight(300);
        filterSheet.setHideable(true);
        filterSheet.setState(TopSheetBehavior.STATE_HIDDEN);

        mBinding.imageViewCloseFilter.setOnClickListener(view -> filterSheet.setState(TopSheetBehavior.STATE_HIDDEN));
        mBinding.imageViewFilter.setOnClickListener(view -> filterSheet.setState(TopSheetBehavior.STATE_EXPANDED));
    }

    public static MainActivity getInstance() {
        return mainActivityWeakReference.get();
    }

}