package com.elvin.expense_analyzer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(
                        SplashScreenActivity.this,
                        SharedPreferencesUtils.checkUser(getApplicationContext()) ? ForemostActivity.class : LoginActivity.class));
                finish();
            }
        }, 2000);
    }
}
