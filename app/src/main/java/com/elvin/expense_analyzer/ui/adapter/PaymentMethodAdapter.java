package com.elvin.expense_analyzer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.endpoint.model.PaymentMethod;

import java.util.List;

/**
 * @author Elvin Shrestha on 2/10/2020
 */
public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.CustomViewHolder> {
    private List<PaymentMethod> paymentMethodList;
    private Context context;
    private PaymentMethodAdapterListener listener;

    public PaymentMethodAdapter(Context context, List<PaymentMethod> paymentMethodList) {
        this.context = context;
        this.paymentMethodList = paymentMethodList;
    }

    public void setPaymentMethodList(List<PaymentMethod> paymentMethodList) {
        this.paymentMethodList = paymentMethodList;
        notifyDataSetChanged();
    }

    public void setPaymentMethodAdapterListener(PaymentMethodAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final PaymentMethod paymentMethod = paymentMethodList.get(position);

        holder.tvPaymentMethodName.setText(paymentMethod.getName());
        holder.ibPaymentMethodDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(paymentMethod.getId());
            }
        });
        holder.ibPaymentMethodEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUpdate(paymentMethod.getId());
            }
        });
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_payment_method_detail, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return paymentMethodList.size();
    }

    public interface PaymentMethodAdapterListener {
        /**
         * Delete callback.
         *
         * @param id Payment Method ID.
         */
        void onDelete(String id);

        /**
         * Update callback.
         *
         * @param id Payment Method ID.
         */
        void onUpdate(String id);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvPaymentMethodName;
        ImageView ivPaymentMethodDetailIcon;
        ImageButton ibPaymentMethodEdit, ibPaymentMethodDelete;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPaymentMethodName = itemView.findViewById(R.id.tvPaymentMethodName);
            ivPaymentMethodDetailIcon = itemView.findViewById(R.id.ivPaymentMethodDetailIcon);
            ibPaymentMethodEdit = itemView.findViewById(R.id.ibPaymentMethodEdit);
            ibPaymentMethodDelete = itemView.findViewById(R.id.ibPaymentMethodDelete);
        }
    }
}
