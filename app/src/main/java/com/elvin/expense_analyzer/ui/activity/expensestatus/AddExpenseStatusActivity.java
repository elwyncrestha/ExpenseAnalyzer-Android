package com.elvin.expense_analyzer.ui.activity.expensestatus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.constants.AppConstant;
import com.elvin.expense_analyzer.endpoint.model.ExpenseStatus;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.service.ExpenseStatusService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class AddExpenseStatusActivity extends AppCompatActivity {

    private EditText etExpenseStatusName;
    private Button btnExpenseStatusSave;
    private ExpenseStatusService service;
    private ExpenseStatus expenseStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_status);

        this.service = RetrofitUtils.getRetrofit().create(ExpenseStatusService.class);
        this.expenseStatus = new ExpenseStatus();

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString(AppConstant.EXPENSE_STATUS_ID) != null) {
            String categoryId = bundle.getString(AppConstant.EXPENSE_STATUS_ID);
            Call<ResponseDto<ExpenseStatus>> call = this.service.getById(
                    SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                    categoryId
            );
            StrictMode.StrictMode();
            try {
                Response<ResponseDto<ExpenseStatus>> response = call.execute();
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Failed to load expense status", Toast.LENGTH_SHORT).show();
                    return;
                }

                this.expenseStatus = response.body().getDetail();
            } catch (IOException e) {
                Log.e("Fetch Expense Status", "Failed to load expense status", e);
                Toast.makeText(getApplicationContext(), "Failed to load expense status", Toast.LENGTH_SHORT).show();
            }
        }

        this.etExpenseStatusName = findViewById(R.id.etExpenseStatusName);
        this.btnExpenseStatusSave = findViewById(R.id.btnExpenseStatusSave);

        this.etExpenseStatusName.setText(this.expenseStatus.getName());

        this.btnExpenseStatusSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etExpenseStatusName.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    etExpenseStatusName.setError("Status Name is required");
                    return;
                }

                expenseStatus.setName(name);
                Call<ResponseDto<ExpenseStatus>> call = expenseStatus.getId() == null
                        ? service.save(
                        SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                        expenseStatus)
                        : service.update(
                        SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                        expenseStatus);
                StrictMode.StrictMode();
                try {
                    Response<ResponseDto<ExpenseStatus>> response = call.execute();
                    if (!response.isSuccessful()) {
                        Toast.makeText(AddExpenseStatusActivity.this, "Failed to save expense status", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ExpenseStatus expenseStatus = response.body().getDetail();
                    startActivity(new Intent(AddExpenseStatusActivity.this, ExpenseStatusActivity.class));
                    finishAffinity();
                } catch (IOException e) {
                    Log.e("Expense Status Save", "Failed to save expense status", e);
                    Toast.makeText(AddExpenseStatusActivity.this, "Failed to save expense status", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
