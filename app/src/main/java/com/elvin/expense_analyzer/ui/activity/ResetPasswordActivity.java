package com.elvin.expense_analyzer.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etResetEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        this.etResetEmail = findViewById(R.id.etResetEmail);
        Button btnResetPasswordVerify = findViewById(R.id.btnResetPasswordVerify);
        TextView tvKnowPasswordLink = findViewById(R.id.tvKnowPasswordLink);

        btnResetPasswordVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etResetEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    etResetEmail.setError("Email address is required");
                    return;
                }

                UserService userService = RetrofitUtils.getRetrofit().create(UserService.class);
                Call<User> userCall = userService.initiateResetPassword(email);
                StrictMode.StrictMode();
                try {
                    Response<User> response = userCall.execute();
                    if (!response.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Failed to initiate user password reset", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startActivity(new Intent(ResetPasswordActivity.this, NewPasswordActivity.class));
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Reset Password Initiation", "Failed to initiate user password reset", e);
                }
            }
        });

        tvKnowPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
