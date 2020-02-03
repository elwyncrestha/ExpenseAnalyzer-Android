package com.elvin.expense_analyzer.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.constants.AppConstant;
import com.elvin.expense_analyzer.endpoint.model.dto.ResetPasswordDto;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPasswordActivity extends AppCompatActivity {

    private EditText etResetToken, etResetNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        final Bundle bundle = getIntent().getExtras();

        this.etResetToken = findViewById(R.id.etResetToken);
        this.etResetNewPassword = findViewById(R.id.etResetNewPassword);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = bundle.getString(AppConstant.RESET_EMAIL_ADDRESS);
                String token = etResetToken.getText().toString();
                String password = etResetNewPassword.getText().toString();

                if (TextUtils.isEmpty(token)) {
                    etResetToken.setError("Token is required");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    etResetNewPassword.setError("Password is required");
                    return;
                }

                UserService userService = RetrofitUtils.getRetrofit().create(UserService.class);
                Call<Void> call = userService.resetPassword(new ResetPasswordDto(email, token, password));
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(NewPasswordActivity.this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(NewPasswordActivity.this, "Successfully reset your password", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(NewPasswordActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("Reset Password", "Failed to reset password", t);
                        Toast.makeText(NewPasswordActivity.this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
