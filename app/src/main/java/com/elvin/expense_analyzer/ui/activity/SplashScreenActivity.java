package com.elvin.expense_analyzer.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.constants.AppConstant;
import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, checkUser() ? MainActivity.class : LoginActivity.class));
                finish();
            }
        }, 2000);
    }

    private boolean checkUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
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
}
