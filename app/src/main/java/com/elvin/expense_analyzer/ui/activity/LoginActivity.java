package com.elvin.expense_analyzer.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.constants.AppConstant;
import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.model.dto.LoginDto;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginUsername, etLoginPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.etLoginUsername = findViewById(R.id.etLoginUsername);
        this.etLoginPassword = findViewById(R.id.etLoginPassword);
        this.btnLogin = findViewById(R.id.btnLogin);

        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etLoginUsername.getText().toString().trim();
                String password = etLoginPassword.getText().toString().trim();

                UserService userService = RetrofitUtils.getRetrofit().create(UserService.class);
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                Call<LoginDto> userCall = userService.login(user);
                StrictMode.StrictMode();
                try {
                    Response<LoginDto> userResponse = userCall.execute();
                    if (!userResponse.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
                    sharedPreferences.edit().putString(
                            AppConstant.AUTHENTICATION_TOKEN,
                            Objects.requireNonNull(userResponse.body()).getToken()
                    ).apply();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("User Login", "Failed to login", e);
                    Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}