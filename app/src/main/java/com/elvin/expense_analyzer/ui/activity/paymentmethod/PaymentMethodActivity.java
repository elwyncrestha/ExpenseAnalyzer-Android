package com.elvin.expense_analyzer.ui.activity.paymentmethod;

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
import com.elvin.expense_analyzer.endpoint.model.PaymentMethod;
import com.elvin.expense_analyzer.endpoint.model.dto.PaymentMethodCountDto;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.service.PaymentMethodService;
import com.elvin.expense_analyzer.ui.adapter.PaymentMethodAdapter;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodActivity extends AppCompatActivity {

    private TextView tvPaymentMethodCount;
    private PaymentMethodService service;
    private RecyclerView rvPaymentMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        this.service = RetrofitUtils.getRetrofit().create(PaymentMethodService.class);

        this.bindControls();
        this.loadCounts();
        this.loadPaymentMethods();
    }

    private void bindControls() {
        this.tvPaymentMethodCount = findViewById(R.id.tvPaymentMethodCount);
        this.rvPaymentMethods = findViewById(R.id.rvPaymentMethods);
    }

    private void loadCounts() {
        Call<ResponseDto<PaymentMethodCountDto>> call =
                this.service.statusCount(SharedPreferencesUtils.getAuthToken(getApplicationContext()));
        call.enqueue(new Callback<ResponseDto<PaymentMethodCountDto>>() {
            @Override
            public void onResponse(Call<ResponseDto<PaymentMethodCountDto>> call, Response<ResponseDto<PaymentMethodCountDto>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(PaymentMethodActivity.this, "Failed to load payment method count", Toast.LENGTH_SHORT).show();
                    return;
                }

                PaymentMethodCountDto dto = response.body().getDetail();
                tvPaymentMethodCount.setText(String.valueOf(dto.getTotalCount()));
            }

            @Override
            public void onFailure(Call<ResponseDto<PaymentMethodCountDto>> call, Throwable t) {
                Log.e("Payment Method Count", "Failed to load payment method count", t);
                Toast.makeText(PaymentMethodActivity.this, "Failed to load payment method count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPaymentMethods() {
        final PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(
                getApplicationContext(),
                fetchPaymentMethods()
        );
        PaymentMethodAdapter.PaymentMethodAdapterListener listener = new PaymentMethodAdapter.PaymentMethodAdapterListener() {
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
                            Toast.makeText(PaymentMethodActivity.this, "Failed to delete payment method", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(PaymentMethodActivity.this, "Successfully deleted payment method", Toast.LENGTH_SHORT).show();

                        List<PaymentMethod> paymentMethodList = fetchPaymentMethods();
                        if (paymentMethodList == null || paymentMethodList.isEmpty()) {
                            Toast.makeText(PaymentMethodActivity.this, "Failed to refresh payment methods", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        paymentMethodAdapter.setPaymentMethodList(paymentMethodList);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("Delete Payment Method", "Failed to delete payment method", t);
                        Toast.makeText(PaymentMethodActivity.this, "Failed to delete payment method", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onUpdate(String id) {
                Intent intent = new Intent(PaymentMethodActivity.this, AddPaymentMethodActivity.class);
                intent.putExtra(AppConstant.PAYMENT_METHOD_ID, id);
                startActivity(intent);
            }
        };
        paymentMethodAdapter.setPaymentMethodAdapterListener(listener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        rvPaymentMethods.setLayoutManager(layoutManager);
        rvPaymentMethods.setHasFixedSize(true);
        rvPaymentMethods.setAdapter(paymentMethodAdapter);
        paymentMethodAdapter.notifyDataSetChanged();
    }

    public final List<PaymentMethod> fetchPaymentMethods() {
        Call<ResponseDto<List<PaymentMethod>>> call =
                this.service.getAll(SharedPreferencesUtils.getAuthToken(getApplicationContext()));
        try {
            StrictMode.StrictMode();
            Response<ResponseDto<List<PaymentMethod>>> response = call.execute();
            if (!response.isSuccessful()) {
                Toast.makeText(PaymentMethodActivity.this, "Failed to load payment methods", Toast.LENGTH_SHORT).show();
                return null;
            }

            return response.body().getDetail();
        } catch (IOException e) {
            Log.e("Payment Method List", "Failed to load payment methods", e);
            Toast.makeText(PaymentMethodActivity.this, "Failed to load payment methods", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.payment_method_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.itemAddPaymentMethod) {
            startActivity(new Intent(getApplicationContext(), AddPaymentMethodActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
