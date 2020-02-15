package com.elvin.expense_analyzer.ui.activity.transaction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.constants.AppConstant;
import com.elvin.expense_analyzer.endpoint.model.Category;
import com.elvin.expense_analyzer.endpoint.model.Expense;
import com.elvin.expense_analyzer.endpoint.model.ExpenseStatus;
import com.elvin.expense_analyzer.endpoint.model.PaymentMethod;
import com.elvin.expense_analyzer.endpoint.model.dto.CategorySearchDto;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.model.enums.CategoryType;
import com.elvin.expense_analyzer.endpoint.service.CategoryService;
import com.elvin.expense_analyzer.endpoint.service.ExpenseStatusService;
import com.elvin.expense_analyzer.endpoint.service.PaymentMethodService;
import com.elvin.expense_analyzer.endpoint.service.TransactionService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class AddTransactionActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Spinner spTransactionType, spTransactionCategory, spTransactionPaymentMethod, spTransactionStatus;
    private EditText etPayeeOrPayer, etTransactionAmount, etTransactionDescription;
    private TextView tvTransactionDateInput, tvTransactionTimeInput;
    private Button btnTransactionSave;
    private TransactionService service;
    private CategoryService categoryService;
    private PaymentMethodService paymentMethodService;
    private ExpenseStatusService expenseStatusService;
    private Expense expense;
    private List<Category> categoryList;
    private List<PaymentMethod> paymentMethodList;
    private List<ExpenseStatus> expenseStatusList;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        this.service = RetrofitUtils.getRetrofit().create(TransactionService.class);
        this.categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
        this.paymentMethodService = RetrofitUtils.getRetrofit().create(PaymentMethodService.class);
        this.expenseStatusService = RetrofitUtils.getRetrofit().create(ExpenseStatusService.class);
        this.expense = new Expense();

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString(AppConstant.TRANSACTION_ID) != null) {
            String transactionId = bundle.getString(AppConstant.TRANSACTION_ID);
            Call<ResponseDto<Expense>> call = this.service.getById(
                    SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                    transactionId
            );
            StrictMode.StrictMode();
            try {
                Response<ResponseDto<Expense>> response = call.execute();
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Failed to load transaction", Toast.LENGTH_SHORT).show();
                    return;
                }

                this.expense = response.body().getDetail();
            } catch (IOException e) {
                Log.e("Fetch Transaction", "Failed to load transaction", e);
                Toast.makeText(getApplicationContext(), "Failed to load transaction", Toast.LENGTH_SHORT).show();
            }
        }

        fetchPaymentMethods();
        fetchExpenseStatuses();
        bindControls();

        // Spinner: Transaction Type
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, CategoryType.values());
        this.spTransactionType.setAdapter(adapter);

        // Spinner: Category
        if (this.expense.getType() != null) {
            this.categoryList = this.fetchCategories(this.expense.getType());
            ArrayAdapter categoryAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    this.categoryList.stream().map(Category::getName).collect(Collectors.toList())
            );
            this.spTransactionCategory.setAdapter(categoryAdapter);
            if (this.categoryList != null && !this.categoryList.isEmpty()) {
                updateCategorySelected();
            }
        }

        // Spinner: Payment method
        ArrayAdapter paymentMethodAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                this.paymentMethodList.stream().map(PaymentMethod::getName).collect(Collectors.toList())
        );
        this.spTransactionPaymentMethod.setAdapter(paymentMethodAdapter);

        // Spinner: Expense status
        ArrayAdapter expenseStatusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                this.expenseStatusList.stream().map(ExpenseStatus::getName).collect(Collectors.toList())
        );
        this.spTransactionStatus.setAdapter(expenseStatusAdapter);

        if (this.expense.getType() != null) {
            int selected = CategoryType.values()[this.expense.getType()].ordinal();
            this.spTransactionType.setSelection(selected);
        }

        if (this.expense.getPaymentMethod() != null) {
            updatePaymentMethodSelected();
        }
        if (this.expense.getStatus() != null) {
            updateExpenseStatusSelected();
        }
        tvTransactionDateInput.setText(
                expense.getDate() == null ? AppConstant.YYYY_MM_DD
                        : new SimpleDateFormat(AppConstant.YYYY_MM_DD, Locale.ENGLISH).format(expense.getDate())
        );
        tvTransactionTimeInput.setText(expense.getTime() == null ? AppConstant.HH_MM : expense.getTime());
        etTransactionAmount.setText(String.valueOf(expense.getAmount() == null ? 0D : expense.getAmount()));
        etPayeeOrPayer.setText(expense.getPayeeOrPayer());
        etTransactionDescription.setText(expense.getDescription());

        tvTransactionDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDatePicker();
            }
        });

        tvTransactionTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTimePicker();
            }
        });

        spTransactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryList = fetchCategories(CategoryType.getEnum(spTransactionType.getSelectedItem().toString()).ordinal());
                ArrayAdapter categoryAdapter = new ArrayAdapter<>(
                        getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        categoryList.stream().map(Category::getName).collect(Collectors.toList())
                );
                spTransactionCategory.setAdapter(categoryAdapter);
                if (categoryList != null && !categoryList.isEmpty()) {
                    updateCategorySelected();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnTransactionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String time, amount, payeeOrPayer, description, type, category, paymentMethod, status;
                try {
                    date = new SimpleDateFormat(AppConstant.YYYY_MM_DD, Locale.ENGLISH).parse(tvTransactionDateInput.getText().toString());
                } catch (ParseException e) {
                    Log.e("Transaction Date Parse", "Failed to parse transaction date", e);
                    return;
                }
                time = tvTransactionTimeInput.getText().toString();
                amount = etTransactionAmount.getText().toString();
                payeeOrPayer = etPayeeOrPayer.getText().toString();
                description = etTransactionDescription.getText().toString();
                type = spTransactionType.getSelectedItem().toString();
                category = spTransactionCategory.getSelectedItem().toString();
                paymentMethod = spTransactionPaymentMethod.getSelectedItem().toString();
                status = spTransactionStatus.getSelectedItem().toString();
                if (TextUtils.isEmpty(amount)) {
                    etTransactionAmount.setError("Amount is required");
                    return;
                } else if (TextUtils.isEmpty(payeeOrPayer)) {
                    etPayeeOrPayer.setError("Payee or Payer is required");
                    return;
                }
                expense.setDate(date);
                expense.setTime(time);
                expense.setAmount(Double.parseDouble(amount));
                expense.setPayeeOrPayer(payeeOrPayer);
                expense.setDescription(description);
                expense.setType(CategoryType.getEnum(type).ordinal());
                for (int i = 0; i < categoryList.size(); i++) {
                    Category c = categoryList.get(i);
                    if (c.getName().equals(category)) {
                        expense.setCategory(c.getId());
                        break;
                    }
                }
                for (int i = 0; i < paymentMethodList.size(); i++) {
                    PaymentMethod c = paymentMethodList.get(i);
                    if (c.getName().equals(paymentMethod)) {
                        expense.setPaymentMethod(c.getId());
                        break;
                    }
                }
                for (int i = 0; i < expenseStatusList.size(); i++) {
                    ExpenseStatus c = expenseStatusList.get(i);
                    if (c.getName().equals(status)) {
                        expense.setStatus(c.getId());
                        break;
                    }
                }
                Call<ResponseDto<Expense>> call = expense.getId() == null
                        ? service.save(
                        SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                        expense)
                        : service.update(
                        SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                        expense);
                StrictMode.StrictMode();
                try {
                    Response<ResponseDto<Expense>> response = call.execute();
                    if (!response.isSuccessful()) {
                        Toast.makeText(AddTransactionActivity.this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(AddTransactionActivity.this, "Successfully saved transaction", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddTransactionActivity.this, TransactionActivity.class));
                } catch (IOException e) {
                    Log.e("Transaction Save", "Failed to save transaction", e);
                    Toast.makeText(AddTransactionActivity.this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        Date date = calendar.getTime();
        tvTransactionDateInput.setText(new SimpleDateFormat(AppConstant.YYYY_MM_DD, Locale.ENGLISH).format(date));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        tvTransactionTimeInput.setText(String.format(Locale.ENGLISH, "%d:%d", hourOfDay, minute));
    }

    private void loadDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(expense.getDate() == null ? new Date() : expense.getDate());
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        if (expense.getTime() != null) {
            String[] time = expense.getTime().split(":");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private List<Category> fetchCategories(Integer type) {
        CategorySearchDto searchDto = new CategorySearchDto();
        searchDto.setType(type);
        Call<ResponseDto<List<Category>>> call = this.categoryService.getAllWithSearch(
                SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                searchDto
        );
        try {
            Response<ResponseDto<List<Category>>> response = call.execute();
            if (!response.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                return new ArrayList<>();
            }

            return response.body().getDetail();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            Log.e("Fetch Categories", "Failed to load categories", e);
        }
        return new ArrayList<>();
    }

    private void fetchPaymentMethods() {
        Call<ResponseDto<List<PaymentMethod>>> paymentMethodCall = paymentMethodService.getAll(
                SharedPreferencesUtils.getAuthToken(getApplicationContext())
        );
        StrictMode.StrictMode();
        try {
            Response<ResponseDto<List<PaymentMethod>>> response = paymentMethodCall.execute();
            if (!response.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Failed to load payment methods", Toast.LENGTH_SHORT).show();
                return;
            }

            this.paymentMethodList = response.body().getDetail();
        } catch (IOException e) {
            Log.e("Fetch Payment Method", "Failed to load payment methods", e);
            Toast.makeText(getApplicationContext(), "Failed to load payment methods", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchExpenseStatuses() {
        Call<ResponseDto<List<ExpenseStatus>>> expenseStatusCall = expenseStatusService.getAll(
                SharedPreferencesUtils.getAuthToken(getApplicationContext())
        );
        StrictMode.StrictMode();
        try {
            Response<ResponseDto<List<ExpenseStatus>>> response = expenseStatusCall.execute();
            if (!response.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Failed to load statuses", Toast.LENGTH_SHORT).show();
                return;
            }

            this.expenseStatusList = response.body().getDetail();
        } catch (IOException e) {
            Log.e("Fetch Expense Status", "Failed to load statuses", e);
            Toast.makeText(getApplicationContext(), "Failed to load statuses", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindControls() {
        this.spTransactionType = findViewById(R.id.spTransactionType);
        this.spTransactionCategory = findViewById(R.id.spTransactionCategory);
        this.spTransactionPaymentMethod = findViewById(R.id.spTransactionPaymentMethod);
        this.spTransactionStatus = findViewById(R.id.spTransactionStatus);
        this.tvTransactionDateInput = findViewById(R.id.tvTransactionDateInput);
        this.tvTransactionTimeInput = findViewById(R.id.tvTransactionTimeInput);
        this.etPayeeOrPayer = findViewById(R.id.etPayeeOrPayer);
        this.etTransactionAmount = findViewById(R.id.etTransactionAmount);
        this.etTransactionDescription = findViewById(R.id.etTransactionDescription);
        this.btnTransactionSave = findViewById(R.id.btnTransactionSave);
    }

    private void updateCategorySelected() {
        int selected = 0;
        Category selectedCategory = gson.fromJson(gson.toJson(this.expense.getCategory()), Category.class);
        if (selectedCategory != null) {
            for (int i = 0; i < this.categoryList.size(); i++) {
                if (this.categoryList.get(i).getName().equals(selectedCategory.getName())) {
                    selected = i;
                    break;
                }
            }
        }
        this.spTransactionCategory.setSelection(selected);
    }

    private void updatePaymentMethodSelected() {
        int selected = 0;
        PaymentMethod selectedPaymentMethod = gson.fromJson(gson.toJson(this.expense.getPaymentMethod()), PaymentMethod.class);
        for (int i = 0; i < this.paymentMethodList.size(); i++) {
            if (this.paymentMethodList.get(i).getName().equals(selectedPaymentMethod.getName())) {
                selected = i;
                break;
            }
        }
        this.spTransactionPaymentMethod.setSelection(selected);
    }

    private void updateExpenseStatusSelected() {
        int selected = 0;
        ExpenseStatus selectedExpenseStatus = gson.fromJson(gson.toJson(this.expense.getStatus()), ExpenseStatus.class);
        for (int i = 0; i < this.expenseStatusList.size(); i++) {
            if (this.expenseStatusList.get(i).getName().equals(selectedExpenseStatus.getName())) {
                selected = i;
                break;
            }
        }
        this.spTransactionStatus.setSelection(selected);
    }

}
