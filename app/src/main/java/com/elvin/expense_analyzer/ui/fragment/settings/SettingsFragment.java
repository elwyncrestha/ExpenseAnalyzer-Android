package com.elvin.expense_analyzer.ui.fragment.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.ui.activity.about.AboutActivity;
import com.elvin.expense_analyzer.ui.activity.category.CategoryActivity;
import com.elvin.expense_analyzer.ui.activity.paymentmethod.PaymentMethodActivity;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private CardView cardCategory, cardPaymentMethod, cardStatus, cardAbout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        this.cardCategory = root.findViewById(R.id.cardCategory);
        this.cardPaymentMethod = root.findViewById(R.id.cardPaymentMethod);
        this.cardStatus = root.findViewById(R.id.cardStatus);
        this.cardAbout = root.findViewById(R.id.cardAbout);

        this.cardCategory.setOnClickListener(this);
        this.cardPaymentMethod.setOnClickListener(this);
        this.cardStatus.setOnClickListener(this);
        this.cardAbout.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardCategory:
                startActivity(new Intent(getContext(), CategoryActivity.class));
                break;
            case R.id.cardPaymentMethod:
                startActivity(new Intent(getContext(), PaymentMethodActivity.class));
                break;
            case R.id.cardAbout:
                startActivity(new Intent(getContext(), AboutActivity.class));
                break;
            default:
                Toast.makeText(getContext(), "Invalid action", Toast.LENGTH_SHORT).show();
        }
    }
}