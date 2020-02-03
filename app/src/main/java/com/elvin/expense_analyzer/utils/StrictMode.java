package com.elvin.expense_analyzer.utils;

/**
 * @author Elvin Shrestha on 2/3/2020
 */
public class StrictMode {

    public static void StrictMode() {
        android.os.StrictMode.ThreadPolicy policy =
                new android.os.StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
        android.os.StrictMode.setThreadPolicy(policy);
    }
}
