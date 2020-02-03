package com.elvin.expense_analyzer.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.constants.AppConstant;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogout, btnLogoutAll;
        btnLogout = findViewById(R.id.btnLogout);
        btnLogoutAll = findViewById(R.id.btnLogoutAll);

        final UserService userService = RetrofitUtils.getRetrofit().create(UserService.class);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = "Bearer " + SharedPreferencesUtils.getAuthToken(getApplicationContext());
                Call<Void> call = v.getId() == R.id.btnLogout
                        ? userService.logout(token)
                        : userService.logoutAll(token);
                try {
                    Response<Void> response = call.execute();
                    if (!response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Failed to logout", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(MainActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
                    sharedPreferences.edit().putString(AppConstant.AUTHENTICATION_TOKEN, null).apply();

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Logout Current", "Failed to logout current device", e);
                    Toast.makeText(MainActivity.this, "Failed to logout", Toast.LENGTH_SHORT).show();
                }
            }
        };
        StrictMode.StrictMode();
        btnLogout.setOnClickListener(listener);
        btnLogoutAll.setOnClickListener(listener);
    }
}
