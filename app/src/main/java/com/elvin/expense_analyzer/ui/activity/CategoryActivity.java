package com.elvin.expense_analyzer.ui.activity;

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
import com.elvin.expense_analyzer.endpoint.model.Category;
import com.elvin.expense_analyzer.endpoint.model.dto.CategoryCountDto;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.service.CategoryService;
import com.elvin.expense_analyzer.ui.adapter.CategoryAdapter;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private TextView tvIncomeCategoryCount, tvExpenseCategoryCount;
    private CategoryService categoryService;
    private RecyclerView rvCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        this.categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);

        this.bindControls();
        this.loadCounts();
        this.loadCategories();
    }

    private void bindControls() {
        this.tvIncomeCategoryCount = findViewById(R.id.tvIncomeCategoryCount);
        this.tvExpenseCategoryCount = findViewById(R.id.tvExpenseCategoryCount);
        this.rvCategories = findViewById(R.id.rvCategories);
    }

    private void loadCounts() {
        Call<ResponseDto<CategoryCountDto>> call =
                this.categoryService.statusCount(SharedPreferencesUtils.getAuthToken(getApplicationContext()));
        call.enqueue(new Callback<ResponseDto<CategoryCountDto>>() {
            @Override
            public void onResponse(Call<ResponseDto<CategoryCountDto>> call, Response<ResponseDto<CategoryCountDto>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CategoryActivity.this, "Failed to load categories count", Toast.LENGTH_SHORT).show();
                    return;
                }

                CategoryCountDto categoryCountDto = response.body().getDetail();
                tvIncomeCategoryCount.setText(String.valueOf(categoryCountDto.getIncomeCount()));
                tvExpenseCategoryCount.setText(String.valueOf(categoryCountDto.getExpenseCount()));
            }

            @Override
            public void onFailure(Call<ResponseDto<CategoryCountDto>> call, Throwable t) {
                Log.e("Category Count", "Failed to load categories count", t);
                Toast.makeText(CategoryActivity.this, "Failed to load categories count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        Call<ResponseDto<List<Category>>> call =
                this.categoryService.getAll(SharedPreferencesUtils.getAuthToken(getApplicationContext()));
        call.enqueue(new Callback<ResponseDto<List<Category>>>() {
            @Override
            public void onResponse(Call<ResponseDto<List<Category>>> call, Response<ResponseDto<List<Category>>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CategoryActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Category> categoryList = response.body().getDetail();
                CategoryAdapter categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.VERTICAL, false);
                rvCategories.setLayoutManager(layoutManager);
                rvCategories.setHasFixedSize(true);
                rvCategories.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseDto<List<Category>>> call, Throwable t) {
                Log.e("Category List", "Failed to load categories", t);
                Toast.makeText(CategoryActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.itemAddCategory) {
            startActivity(new Intent(getApplicationContext(), AddCategoryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
