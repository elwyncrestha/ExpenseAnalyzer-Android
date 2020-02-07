package com.elvin.expense_analyzer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterFirstName, etRegisterMiddleName, etRegisterLastName, etRegisterEmail,
            etRegisterUsername, etRegisterPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView tvHaveAccount = findViewById(R.id.tvHaveAccount);
        Button btnRegister = findViewById(R.id.btnRegister);
        this.etRegisterFirstName = findViewById(R.id.etRegisterFirstName);
        this.etRegisterMiddleName = findViewById(R.id.etRegisterMiddleName);
        this.etRegisterLastName = findViewById(R.id.etRegisterLastName);
        this.etRegisterEmail = findViewById(R.id.etRegisterEmail);
        this.etRegisterUsername = findViewById(R.id.etRegisterUsername);
        this.etRegisterPassword = findViewById(R.id.etRegisterPassword);

        tvHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etRegisterFirstName.getText().toString();
                String middleName = etRegisterMiddleName.getText().toString();
                String lastName = etRegisterLastName.getText().toString();
                String email = etRegisterEmail.getText().toString();
                String username = etRegisterUsername.getText().toString();
                String password = etRegisterPassword.getText().toString();
                if (TextUtils.isEmpty(firstName)) {
                    etRegisterFirstName.setError("First name is required");
                    return;
                } else if (TextUtils.isEmpty(lastName)) {
                    etRegisterLastName.setError("Last name is required");
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    etRegisterEmail.setError("Email is required");
                    return;
                } else if (TextUtils.isEmpty(username)) {
                    etRegisterUsername.setError("Username is required");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    etRegisterPassword.setError("Password is required");
                    return;
                }

                User user = new User(firstName, middleName, lastName, email, username, password, null, null);

                UserService userService = RetrofitUtils.getRetrofit().create(UserService.class);
                Call<ResponseDto<User>> userCall = userService.save(user);
                userCall.enqueue(new Callback<ResponseDto<User>>() {
                    @Override
                    public void onResponse(Call<ResponseDto<User>> call, Response<ResponseDto<User>> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Failed to register!!!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        etRegisterFirstName.setText("");
                        etRegisterMiddleName.setText("");
                        etRegisterLastName.setText("");
                        etRegisterEmail.setText("");
                        etRegisterUsername.setText("");
                        etRegisterPassword.setText("");

                        Toast.makeText(RegisterActivity.this, "You can sign in now!!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResponseDto<User>> call, Throwable t) {
                        Log.e("User Registration", "Failed to register", t);
                        Toast.makeText(RegisterActivity.this, "Failed to register!!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
