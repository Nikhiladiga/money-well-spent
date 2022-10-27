package com.nikhil.moneywellspent.util;

import java.util.Objects;

public class SharedUtil {

    private static SharedUtil instance;

    public static synchronized SharedUtil getInstance() {
        if (instance == null) {
            instance = new SharedUtil();
        }
        return instance;
    }

    public void saveCategory(String category) {
        if (!Objects.requireNonNull(SharedPrefHelper.getCategories()).contains(category)) {
            SharedPrefHelper.addCategory(category);
        }
    }
}
