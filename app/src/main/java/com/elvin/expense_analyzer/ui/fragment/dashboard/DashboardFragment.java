package com.elvin.expense_analyzer.ui.fragment.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.endpoint.model.Expense;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.model.dto.TransactionDurationDto;
import com.elvin.expense_analyzer.endpoint.service.TransactionService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private TransactionService transactionService;
    private TextView dashboardCountToday, dashboardCountThisWeek, dashboardCountThisMonth, dashboardCountThisYear;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dashboardCountToday = root.findViewById(R.id.dashboardCountToday);
        dashboardCountThisWeek = root.findViewById(R.id.dashboardCountThisWeek);
        dashboardCountThisMonth = root.findViewById(R.id.dashboardCountThisMonth);
        dashboardCountThisYear = root.findViewById(R.id.dashboardCountThisYear);

        transactionService = RetrofitUtils.getRetrofit().create(TransactionService.class);
        loadCounts();

        return root;
    }

    private void loadCounts() {
        Call<ResponseDto<TransactionDurationDto>> call = transactionService.getByDurations(
                SharedPreferencesUtils.getAuthToken(getContext())
        );
        call.enqueue(new Callback<ResponseDto<TransactionDurationDto>>() {
            @Override
            public void onResponse(Call<ResponseDto<TransactionDurationDto>> call, Response<ResponseDto<TransactionDurationDto>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Failed to load counts", Toast.LENGTH_SHORT).show();
                    return;
                }
                TransactionDurationDto dto = response.body().getDetail();
                Double todayIncome = dto.getToday().getIncome().stream().mapToDouble(Expense::getAmount).sum();
                Double todayExpense = dto.getToday().getExpense().stream().mapToDouble(Expense::getAmount).sum();

                Double weekIncome = dto.getThisWeek().getIncome().stream().mapToDouble(Expense::getAmount).sum();
                Double weekExpense = dto.getThisWeek().getExpense().stream().mapToDouble(Expense::getAmount).sum();

                Double monthIncome = dto.getThisMonth().getIncome().stream().mapToDouble(Expense::getAmount).sum();
                Double monthExpense = dto.getThisMonth().getExpense().stream().mapToDouble(Expense::getAmount).sum();

                Double yearIncome = dto.getThisYear().getIncome().stream().mapToDouble(Expense::getAmount).sum();
                Double yearExpense = dto.getThisYear().getExpense().stream().mapToDouble(Expense::getAmount).sum();

                dashboardCountToday.setText(String.format(Locale.ENGLISH, "%.2f / -%.2f", todayIncome, todayExpense));
                dashboardCountThisWeek.setText(String.format(Locale.ENGLISH, "%.2f / -%.2f", weekIncome, weekExpense));
                dashboardCountThisMonth.setText(String.format(Locale.ENGLISH, "%.2f / -%.2f", monthIncome, monthExpense));
                dashboardCountThisYear.setText(String.format(Locale.ENGLISH, "%.2f / -%.2f", yearIncome, yearExpense));
            }

            @Override
            public void onFailure(Call<ResponseDto<TransactionDurationDto>> call, Throwable t) {
                Log.e("Dashboard Count", "Failed to load counts", t);
                Toast.makeText(getContext(), "Failed to load counts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}