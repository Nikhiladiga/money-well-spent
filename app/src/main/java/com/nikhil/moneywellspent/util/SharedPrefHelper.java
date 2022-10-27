package com.nikhil.moneywellspent.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class SharedPrefHelper {

    private static SharedPreferences sharedPreferences;

    public static void initSharedPrefHelper(SharedPreferences sharedPreferences) {
        SharedPrefHelper.sharedPreferences = sharedPreferences;
        if (SharedPrefHelper.getCategories() == null) {
            List<String> categories = new ArrayList<>();
            categories.add("Food");
            categories.add("Grocery");
            categories.add("Entertainment");
            categories.add("Investment");
            categories.add("Sports");
            categories.add("Fuel");
            categories.add("General");
            categories.add("Holidays");
            categories.add("Travel");
            categories.add("Gifts");
            categories.add("Shopping");
            categories.add("Clothes");
            categories.add("Movies");
            categories.add("Salary");
            categories.add("Custom");

            try {
                String jsonString = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .writeValueAsString(categories);

                SharedPrefHelper.setCategories(jsonString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getUsername() {
        return sharedPreferences.getString("username", null);
    }

    public static void setUsername(String username, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.apply();
    }

    public static List<String> getCategories() {
        String categories = sharedPreferences.getString("categories", null);
        if (categories != null) {
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                return objectMapper.readValue(categories, new TypeReference<List<String>>() {
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void setCategories(String categories) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("categories", categories);
        editor.apply();
    }

    public static String getBalanceLimit() {
        return sharedPreferences.getString("balanceLimit", null);
    }

    public static void setBalanceLimit(String balanceLimit) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("balanceLimit", balanceLimit);
        editor.apply();
    }

    public static String getExpenseLimit() {
        return sharedPreferences.getString("expenseLimit", null);
    }

    public static void setExpenseLimit(String expenseLimit) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("expenseLimit", expenseLimit);
        editor.apply();
    }

    public static String getMonthStartDay() {
        return sharedPreferences.getString("monthStartDay", "1");
    }

    public static void setMonthStartDay(String monthStartDay) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("monthStartDay", monthStartDay);
        editor.apply();
    }

    public static void addCategory(String category) {
        List<String> categories = getCategories();
        if (categories != null) {
            categories.add(category);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String newJsonString = null;
            try {
                newJsonString = objectMapper.writeValueAsString(categories);
                editor.putString("categories", newJsonString);
                editor.apply();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
