package com.elvin.expense_analyzer.ui.activity.expensestatus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.constants.AppConstant;
import com.elvin.expense_analyzer.endpoint.model.ExpenseStatus;
import com.elvin.expense_analyzer.endpoint.model.dto.ExpenseStatusCountDto;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.service.ExpenseStatusService;
import com.elvin.expense_analyzer.ui.activity.main.ForemostActivity;
import com.elvin.expense_analyzer.ui.adapter.ExpenseStatusAdapter;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseStatusActivity extends AppCompatActivity {

    private TextView tvStatusCount;
    private RecyclerView rvStatuses;
    private ExpenseStatusService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_status);

        this.service = RetrofitUtils.getRetrofit().create(ExpenseStatusService.class);

        this.bindControls();
        this.loadCounts();
        this.loadExpenseStatuses();
    }

    private void bindControls() {
        this.tvStatusCount = findViewById(R.id.tvStatusCount);
        this.rvStatuses = findViewById(R.id.rvStatuses);
    }

    private void loadCounts() {
        Call<ResponseDto<ExpenseStatusCountDto>> call =
                this.service.statusCount(SharedPreferencesUtils.getAuthToken(getApplicationContext()));
        call.enqueue(new Callback<ResponseDto<ExpenseStatusCountDto>>() {
            @Override
            public void onResponse(Call<ResponseDto<ExpenseStatusCountDto>> call, Response<ResponseDto<ExpenseStatusCountDto>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(ExpenseStatusActivity.this, "Failed to load expense status count", Toast.LENGTH_SHORT).show();
                    return;
                }

                ExpenseStatusCountDto dto = response.body().getDetail();
                tvStatusCount.setText(String.valueOf(dto.getTotalCount()));
            }

            @Override
            public void onFailure(Call<ResponseDto<ExpenseStatusCountDto>> call, Throwable t) {
                Log.e("Expense StatusCount", "Failed to load expense status count", t);
                Toast.makeText(ExpenseStatusActivity.this, "Failed to load expense status count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExpenseStatuses() {
        final ExpenseStatusAdapter expenseStatusAdapter = new ExpenseStatusAdapter(
                getApplicationContext(),
                fetchExpenseStatuses()
        );
        ExpenseStatusAdapter.ExpenseStatusAdapterListener listener = new ExpenseStatusAdapter.ExpenseStatusAdapterListener() {
            @Override
            public void onDelete(String id) {
                Call<Void> call = service.delete(
                        SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                        id
                );
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(ExpenseStatusActivity.this, "Failed to delete expense status", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(ExpenseStatusActivity.this, "Successfully deleted expense status", Toast.LENGTH_SHORT).show();

                        List<ExpenseStatus> expenseStatusList = fetchExpenseStatuses();
                        if (expenseStatusList == null || expenseStatusList.isEmpty()) {
                            Toast.makeText(ExpenseStatusActivity.this, "Failed to refresh expense status", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        expenseStatusAdapter.setExpenseStatusList(expenseStatusList);
                        loadCounts();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("Delete Expense Status", "Failed to delete expense status", t);
                        Toast.makeText(ExpenseStatusActivity.this, "Failed to delete expense status", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onUpdate(String id) {
                Intent intent = new Intent(ExpenseStatusActivity.this, AddExpenseStatusActivity.class);
                intent.putExtra(AppConstant.EXPENSE_STATUS_ID, id);
                startActivity(intent);
            }
        };
        expenseStatusAdapter.setExpenseStatusAdapterListener(listener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        rvStatuses.setLayoutManager(layoutManager);
        rvStatuses.setHasFixedSize(true);
        rvStatuses.setAdapter(expenseStatusAdapter);
        expenseStatusAdapter.notifyDataSetChanged();
    }

    public final List<ExpenseStatus> fetchExpenseStatuses() {
        Call<ResponseDto<List<ExpenseStatus>>> call =
                this.service.getAll(SharedPreferencesUtils.getAuthToken(getApplicationContext()));
        try {
            StrictMode.StrictMode();
            Response<ResponseDto<List<ExpenseStatus>>> response = call.execute();
            if (!response.isSuccessful()) {
                Toast.makeText(ExpenseStatusActivity.this, "Failed to load expense statuses", Toast.LENGTH_SHORT).show();
                return null;
            }

            return response.body().getDetail();
        } catch (IOException e) {
            Log.e("Expense Status List", "Failed to load expense statuses", e);
            Toast.makeText(ExpenseStatusActivity.this, "Failed to load expense statuses", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expense_status_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.itemAddExpenseStatus) {
            startActivity(new Intent(getApplicationContext(), AddExpenseStatusActivity.class));
            return true;
        } else if (item.getItemId() == R.id.itemGoBack) {
            startActivity(new Intent(getApplicationContext(), ForemostActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
