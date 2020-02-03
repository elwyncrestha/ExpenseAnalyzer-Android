package com.elvin.expense_analyzer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.elvin.expense_analyzer.constants.AppConstant;
import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.service.UserService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Elvin Shrestha on 2/3/2020
 */
public class SharedPreferencesUtils {

    public static boolean checkUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstant.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString(AppConstant.AUTHENTICATION_TOKEN, null);

        UserService userService = RetrofitUtils.getRetrofit().create(UserService.class);
        Call<User> userCall = userService.getAuthenticated("Bearer " + token);
        StrictMode.StrictMode();
        try {
            Response<User> response = userCall.execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("User Authentication", "Failed to get authenticated user", e);
            return false;
        }
    }

    public static String getAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstant.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.AUTHENTICATION_TOKEN, null);
    }
}
