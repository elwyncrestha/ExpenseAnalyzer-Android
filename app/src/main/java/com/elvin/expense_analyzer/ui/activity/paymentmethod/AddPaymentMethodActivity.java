package com.elvin.expense_analyzer.ui.activity.paymentmethod;

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
import com.elvin.expense_analyzer.endpoint.model.PaymentMethod;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.service.PaymentMethodService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class AddPaymentMethodActivity extends AppCompatActivity {

    private EditText etPaymentMethodName;
    private Button btnPaymentMethodSave;
    private PaymentMethodService service;
    private PaymentMethod paymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);

        this.service = RetrofitUtils.getRetrofit().create(PaymentMethodService.class);
        this.paymentMethod = new PaymentMethod();

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString(AppConstant.PAYMENT_METHOD_ID) != null) {
            String categoryId = bundle.getString(AppConstant.PAYMENT_METHOD_ID);
            Call<ResponseDto<PaymentMethod>> call = this.service.getById(
                    SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                    categoryId
            );
            StrictMode.StrictMode();
            try {
                Response<ResponseDto<PaymentMethod>> response = call.execute();
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Failed to load payment method", Toast.LENGTH_SHORT).show();
                    return;
                }

                this.paymentMethod = response.body().getDetail();
            } catch (IOException e) {
                Log.e("Fetch Payment Method", "Failed to load payment method", e);
                Toast.makeText(getApplicationContext(), "Failed to load payment method", Toast.LENGTH_SHORT).show();
            }
        }

        this.etPaymentMethodName = findViewById(R.id.etPaymentMethodName);
        this.btnPaymentMethodSave = findViewById(R.id.btnPaymentMethodSave);

        this.etPaymentMethodName.setText(this.paymentMethod.getName());

        this.btnPaymentMethodSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etPaymentMethodName.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    etPaymentMethodName.setError("Payment Method Name is required");
                    return;
                }

                paymentMethod.setName(name);
                Call<ResponseDto<PaymentMethod>> call = paymentMethod.getId() == null
                        ? service.save(
                        SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                        paymentMethod)
                        : service.update(
                        SharedPreferencesUtils.getAuthToken(getApplicationContext()),
                        paymentMethod);
                StrictMode.StrictMode();
                try {
                    Response<ResponseDto<PaymentMethod>> response = call.execute();
                    if (!response.isSuccessful()) {
                        Toast.makeText(AddPaymentMethodActivity.this, "Failed to save payment method", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(AddPaymentMethodActivity.this, "Successfully saved payment method", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddPaymentMethodActivity.this, PaymentMethodActivity.class));
                } catch (IOException e) {
                    Log.e("Payment Method Save", "Failed to save payment method", e);
                    Toast.makeText(AddPaymentMethodActivity.this, "Failed to save payment method", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
