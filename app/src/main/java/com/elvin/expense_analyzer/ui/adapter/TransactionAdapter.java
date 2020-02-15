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
import com.elvin.expense_analyzer.endpoint.model.Expense;
import com.elvin.expense_analyzer.endpoint.model.enums.CategoryType;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author Elvin Shrestha on 2/15/2020
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.CustomViewHolder> {
    private List<Expense> expenseList;
    private Context context;
    private TransactionAdapter.TransactionAdapterListener listener;

    public TransactionAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
        notifyDataSetChanged();
    }

    public void setTransactionAdapterListener(TransactionAdapter.TransactionAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.CustomViewHolder holder, int position) {
        final Expense expense = expenseList.get(position);

        int textColor = context.getResources().getColor(
                CategoryType.values()[expense.getType()].equals(CategoryType.INCOME)
                        ? R.color.fragmentTextColor
                        : R.color.fragmentTextColorNegative
        );
        holder.tvTransactionPayeePayer.setText(expense.getPayeeOrPayer());
        holder.tvTransactionPayeePayer.setTextColor(textColor);
        holder.tvTransactionAmount.setText(String.valueOf(expense.getAmount()));
        holder.tvTransactionAmount.setTextColor(textColor);
        holder.tvTransactionDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(expense.getDate()));
        holder.tvTransactionDate.setTextColor(textColor);
        holder.ivTransactionDetailIcon.setImageResource(
                CategoryType.values()[expense.getType()].equals(CategoryType.INCOME)
                        ? R.drawable.ic_multiline_chart_black_24dp
                        : R.drawable.ic_show_chart_black_60dp
        );
        holder.ibTransactionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(expense.getId());
            }
        });
        holder.ibTransactionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUpdate(expense.getId());
            }
        });
    }

    @NonNull
    @Override
    public TransactionAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_transaction_detail, parent, false);
        return new TransactionAdapter.CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public interface TransactionAdapterListener {
        /**
         * Delete callback.
         *
         * @param id Transaction ID.
         */
        void onDelete(String id);

        /**
         * Update callback.
         *
         * @param id Transaction ID.
         */
        void onUpdate(String id);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionPayeePayer, tvTransactionAmount, tvTransactionDate;
        ImageButton ibTransactionEdit, ibTransactionDelete;
        ImageView ivTransactionDetailIcon;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransactionPayeePayer = itemView.findViewById(R.id.tvTransactionPayeePayer);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            ivTransactionDetailIcon = itemView.findViewById(R.id.ivTransactionDetailIcon);
            ibTransactionEdit = itemView.findViewById(R.id.ibTransactionEdit);
            ibTransactionDelete = itemView.findViewById(R.id.ibTransactionDelete);
        }
    }
}
