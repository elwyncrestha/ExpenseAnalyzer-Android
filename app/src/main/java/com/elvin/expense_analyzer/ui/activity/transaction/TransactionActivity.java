package com.elvin.expense_analyzer.ui.activity.transaction;

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
import com.elvin.expense_analyzer.endpoint.model.Expense;
import com.elvin.expense_analyzer.endpoint.model.dto.ExpenseCountDto;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.service.TransactionService;
import com.elvin.expense_analyzer.ui.adapter.TransactionAdapter;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionActivity extends AppCompatActivity {

    private TextView tvIncomeTransactionCount, tvExpenseTransactionCount;
    private RecyclerView rvTransactions;
    private TransactionService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        this.service = RetrofitUtils.getRetrofit().create(TransactionService.class);

        this.bindControls();
        this.loadCounts();
        this.loadTransactions();
    }

    private void bindControls() {
        this.tvIncomeTransactionCount = findViewById(R.id.tvIncomeTransactionCount);
        this.tvExpenseTransactionCount = findViewById(R.id.tvExpenseTransactionCount);
        this.rvTransactions = findViewById(R.id.rvTransactions);
    }

    private void loadCounts() {
        Call<ResponseDto<ExpenseCountDto>> call =
                this.service.statusCount(SharedPreferencesUtils.getAuthToken(getApplicationContext()));
        call.enqueue(new Callback<ResponseDto<ExpenseCountDto>>() {
            @Override
            public void onResponse(Call<ResponseDto<ExpenseCountDto>> call, Response<ResponseDto<ExpenseCountDto>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(TransactionActivity.this, "Failed to load transaction count", Toast.LENGTH_SHORT).show();
                    return;
                }

                ExpenseCountDto dto = response.body().getDetail();
                tvIncomeTransactionCount.setText(String.valueOf(dto.getIncomeCount()));
                tvExpenseTransactionCount.setText(String.valueOf(dto.getExpenseCount()));
            }

            @Override
            public void onFailure(Call<ResponseDto<ExpenseCountDto>> call, Throwable t) {
                Log.e("Transaction Count", "Failed to load transaction count", t);
                Toast.makeText(TransactionActivity.this, "Failed to load transaction count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTransactions() {
        final TransactionAdapter transactionAdapter = new TransactionAdapter(
                getApplicationContext(),
                fetchTransaction()
        );
        TransactionAdapter.TransactionAdapterListener listener = new TransactionAdapter.TransactionAdapterListener() {
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
                            Toast.makeText(TransactionActivity.this, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(TransactionActivity.this, "Successfully deleted transaction", Toast.LENGTH_SHORT).show();

                        List<Expense> expenseList = fetchTransaction();
                        if (expenseList == null || expenseList.isEmpty()) {
                            Toast.makeText(TransactionActivity.this, "Failed to refresh transactions", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        transactionAdapter.setExpenseList(expenseList);
                        loadCounts();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("Delete Transaction", "Failed to delete transaction", t);
                        Toast.makeText(TransactionActivity.this, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onUpdate(String id) {
                Intent intent = new Intent(TransactionActivity.this, AddTransactionActivity.class);
                intent.putExtra(AppConstant.TRANSACTION_ID, id);
                startActivity(intent);
            }
        };
        transactionAdapter.setTransactionAdapterListener(listener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        rvTransactions.setLayoutManager(layoutManager);
        rvTransactions.setHasFixedSize(true);
        rvTransactions.setAdapter(transactionAdapter);
        transactionAdapter.notifyDataSetChanged();
    }

    public final List<Expense> fetchTransaction() {
        Call<ResponseDto<List<Expense>>> call =
                this.service.getAll(SharedPreferencesUtils.getAuthToken(getApplicationContext()));
        try {
            StrictMode.StrictMode();
            Response<ResponseDto<List<Expense>>> response = call.execute();
            if (!response.isSuccessful()) {
                Toast.makeText(TransactionActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                return null;
            }

            return response.body().getDetail();
        } catch (IOException e) {
            Log.e("Transaction List", "Failed to load transactions", e);
            Toast.makeText(TransactionActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transaction_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.itemAddTransaction) {
            startActivity(new Intent(getApplicationContext(), AddTransactionActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
