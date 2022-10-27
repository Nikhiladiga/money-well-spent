package com.nikhil.moneywellspent.util;

public class StringUtils {

    public static StringUtils instance;

    public static synchronized StringUtils getInstance() {
        if (instance == null) {
            instance = new StringUtils();
        }
        return instance;
    }

    public String convertDoubleMoneyToString(double amount, int flag) {
        if (flag == 1) {
            return "₹" + amount;
        } else {
            return "" + amount;
        }
    }

    public Double convertStringAmountToDouble(String amount) {
        return Double.valueOf(amount);
    }
}
