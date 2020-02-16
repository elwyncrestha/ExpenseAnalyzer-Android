package com.elvin.expense_analyzer.ui.fragment.dashboard;

import android.graphics.Color;
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
import com.elvin.expense_analyzer.endpoint.model.dto.CategoryCountDto;
import com.elvin.expense_analyzer.endpoint.model.dto.ExpenseCountDto;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.model.dto.TransactionDurationDto;
import com.elvin.expense_analyzer.endpoint.service.CategoryService;
import com.elvin.expense_analyzer.endpoint.service.TransactionService;
import com.elvin.expense_analyzer.ui.listener.ShakeListener;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private TransactionService transactionService;
    private CategoryService categoryService;
    private TextView dashboardCountToday, dashboardCountThisWeek, dashboardCountThisMonth, dashboardCountThisYear;
    private PieChart pieChartCategory, pieChartExpenseType;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dashboardCountToday = root.findViewById(R.id.dashboardCountToday);
        dashboardCountThisWeek = root.findViewById(R.id.dashboardCountThisWeek);
        dashboardCountThisMonth = root.findViewById(R.id.dashboardCountThisMonth);
        dashboardCountThisYear = root.findViewById(R.id.dashboardCountThisYear);
        pieChartCategory = root.findViewById(R.id.pieChartCategory);
        pieChartExpenseType = root.findViewById(R.id.pieChartExpenseType);

        transactionService = RetrofitUtils.getRetrofit().create(TransactionService.class);
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
        loadCounts();
        loadPieChartCategory();
        loadPieChartExpenseType();
        refreshOnShake();

        return root;
    }

    private void loadPieChartExpenseType() {
        Call<ResponseDto<ExpenseCountDto>> call =
                this.transactionService.statusCount(SharedPreferencesUtils.getAuthToken(getContext()));
        call.enqueue(new Callback<ResponseDto<ExpenseCountDto>>() {
            @Override
            public void onResponse(Call<ResponseDto<ExpenseCountDto>> call, Response<ResponseDto<ExpenseCountDto>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Failed to load expense count", Toast.LENGTH_SHORT).show();
                    return;
                }

                ExpenseCountDto expenseCountDto = response.body().getDetail();
                List<PieEntry> pieEntryList = new ArrayList<>();
                pieEntryList.add(new PieEntry(expenseCountDto.getIncomeCount(), "Income"));
                pieEntryList.add(new PieEntry(expenseCountDto.getExpenseCount(), "Expense"));
                PieDataSet set = new PieDataSet(pieEntryList, "Expense Type Visualization");
                set.setColors(Color.GRAY, Color.RED);
                PieData data = new PieData(set);
                pieChartExpenseType.setData(data);
                Description description = new Description();
                description.setText("Total Expense VS Income Transactions Counts");
                pieChartExpenseType.setDescription(description);
                pieChartExpenseType.invalidate();
            }

            @Override
            public void onFailure(Call<ResponseDto<ExpenseCountDto>> call, Throwable t) {
                Log.e("Expense Count", "Failed to load expense count", t);
                Toast.makeText(getContext(), "Failed to load expense count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPieChartCategory() {
        Call<ResponseDto<CategoryCountDto>> call =
                this.categoryService.statusCount(SharedPreferencesUtils.getAuthToken(getContext()));
        call.enqueue(new Callback<ResponseDto<CategoryCountDto>>() {
            @Override
            public void onResponse(Call<ResponseDto<CategoryCountDto>> call, Response<ResponseDto<CategoryCountDto>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Failed to load categories count", Toast.LENGTH_SHORT).show();
                    return;
                }

                CategoryCountDto categoryCountDto = response.body().getDetail();
                List<PieEntry> pieEntryList = new ArrayList<>();
                pieEntryList.add(new PieEntry(categoryCountDto.getIncomeCount(), "Income"));
                pieEntryList.add(new PieEntry(categoryCountDto.getExpenseCount(), "Expense"));
                PieDataSet set = new PieDataSet(pieEntryList, "Category Visualization");
                set.setColors(Color.GRAY, Color.RED);
                PieData data = new PieData(set);
                pieChartCategory.setData(data);
                Description description = new Description();
                description.setText("Expense VS Income Categories");
                pieChartCategory.setDescription(description);
                pieChartCategory.invalidate();
            }

            @Override
            public void onFailure(Call<ResponseDto<CategoryCountDto>> call, Throwable t) {
                Log.e("Category Count", "Failed to load categories count", t);
                Toast.makeText(getContext(), "Failed to load categories count", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void refreshOnShake() {
        ShakeListener shakeListener = new ShakeListener(getContext());
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                Toast.makeText(getContext(), "Refreshing data!!!", Toast.LENGTH_SHORT).show();
                loadCounts();
                loadPieChartCategory();
                loadPieChartExpenseType();
            }
        });
    }
}