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
import com.elvin.expense_analyzer.endpoint.model.ExpenseStatus;

import java.util.List;

/**
 * @author Elvin Shrestha on 2/10/2020
 */
public class ExpenseStatusAdapter extends RecyclerView.Adapter<ExpenseStatusAdapter.CustomViewHolder> {
    private List<ExpenseStatus> expenseStatusList;
    private Context context;
    private ExpenseStatusAdapterListener listener;

    public ExpenseStatusAdapter(Context context, List<ExpenseStatus> expenseStatusList) {
        this.context = context;
        this.expenseStatusList = expenseStatusList;
    }

    public void setExpenseStatusList(List<ExpenseStatus> expenseStatusList) {
        this.expenseStatusList = expenseStatusList;
        notifyDataSetChanged();
    }

    public void setExpenseStatusAdapterListener(ExpenseStatusAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final ExpenseStatus expenseStatus = expenseStatusList.get(position);

        holder.tvExpenseStatusName.setText(expenseStatus.getName());
        holder.ibExpenseStatusDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(expenseStatus.getId());
            }
        });
        holder.ibExpenseStatusEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUpdate(expenseStatus.getId());
            }
        });
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expense_status_detail, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return expenseStatusList.size();
    }

    public interface ExpenseStatusAdapterListener {
        /**
         * Delete callback.
         *
         * @param id Expense Status ID.
         */
        void onDelete(String id);

        /**
         * Update callback.
         *
         * @param id Expense Status ID.
         */
        void onUpdate(String id);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvExpenseStatusName;
        ImageView ivExpenseStatusDetailIcon;
        ImageButton ibExpenseStatusEdit, ibExpenseStatusDelete;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvExpenseStatusName = itemView.findViewById(R.id.tvExpenseStatusName);
            ivExpenseStatusDetailIcon = itemView.findViewById(R.id.ivExpenseStatusDetailIcon);
            ibExpenseStatusEdit = itemView.findViewById(R.id.ibExpenseStatusEdit);
            ibExpenseStatusDelete = itemView.findViewById(R.id.ibExpenseStatusDelete);
        }
    }
}
