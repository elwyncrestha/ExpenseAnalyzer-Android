package com.elvin.expense_analyzer.ui.activity.category;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.constants.AppConstant;
import com.elvin.expense_analyzer.endpoint.model.Category;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.model.enums.CategoryType;
import com.elvin.expense_analyzer.endpoint.service.CategoryService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText etCategoryName;
    private Spinner spCategoryType;
    private Button btnCategorySave;
    private CategoryService categoryService;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        this.categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
        this.category = new Category();

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString(AppConstant.CATEGORY_ID) != null) {
            String categoryId = bundle.getString(AppConstant.CATEGORY_ID);
            Call<ResponseDto<Category>> call = this.categoryService.getById(
                    SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                    categoryId
            );
            StrictMode.StrictMode();
            try {
                Response<ResponseDto<Category>> response = call.execute();
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Failed to load category", Toast.LENGTH_SHORT).show();
                    return;
                }

                this.category = response.body().getDetail();
            } catch (IOException e) {
                Log.e("Fetch Category", "Failed to load category", e);
                Toast.makeText(getApplicationContext(), "Failed to load category", Toast.LENGTH_SHORT).show();
            }
        }

        this.etCategoryName = findViewById(R.id.etCategoryName);
        this.spCategoryType = findViewById(R.id.spCategoryType);
        this.btnCategorySave = findViewById(R.id.btnCategorySave);

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, CategoryType.values());
        this.spCategoryType.setAdapter(adapter);

        this.etCategoryName.setText(this.category.getName());
        if (this.category.getType() != null) {
            int selected = CategoryType.values()[this.category.getType()].ordinal();
            this.spCategoryType.setSelection(selected);
        }

        this.btnCategorySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etCategoryName.getText().toString();
                String type = spCategoryType.getSelectedItem().toString();

                if (TextUtils.isEmpty(name)) {
                    etCategoryName.setError("Category Name is required");
                    return;
                }

                category.setName(name);
                category.setType(CategoryType.getEnum(type).ordinal());
                Call<ResponseDto<Category>> call = category.getId() == null
                        ? categoryService.save(
                        SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                        category)
                        : categoryService.update(
                        SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                        category);
                StrictMode.StrictMode();
                try {
                    Response<ResponseDto<Category>> response = call.execute();
                    if (!response.isSuccessful()) {
                        Toast.makeText(AddCategoryActivity.this, "Failed to save category", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(AddCategoryActivity.this, "Successfully saved category", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddCategoryActivity.this, CategoryActivity.class));
                    finishAffinity();
                } catch (IOException e) {
                    Log.e("Category Save", "Failed to save category", e);
                    Toast.makeText(AddCategoryActivity.this, "Failed to save category", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.itemGoBack) {
            startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
