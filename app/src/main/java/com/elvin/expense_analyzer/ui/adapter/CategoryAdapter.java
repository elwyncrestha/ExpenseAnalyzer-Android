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
import com.elvin.expense_analyzer.endpoint.model.Category;
import com.elvin.expense_analyzer.endpoint.model.enums.CategoryType;

import java.util.List;

/**
 * @author Elvin Shrestha on 2/9/2020
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CustomViewHolder> {

    private List<Category> categoryList;
    private Context context;
    private CategoryAdapterListener listener;

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    public void setCategoryAdapterListener(CategoryAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final Category category = categoryList.get(position);

        holder.tvCategoryName.setText(category.getName());
        holder.tvCategoryType.setText(CategoryType.values()[category.getType()].toString());
        holder.ivCategoryDetailIcon.setImageResource(
                CategoryType.values()[category.getType()].equals(CategoryType.INCOME)
                        ? R.drawable.ic_attach_money_black_24dp
                        : R.drawable.ic_money_off_black_24dp
        );
        holder.ibCategoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(category.getId());
            }
        });
        holder.ibCategoryEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUpdate(category.getId());
            }
        });
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_detail, parent, false);
        return new CustomViewHolder(view);
    }

    public interface CategoryAdapterListener {
        /**
         * Delete callback.
         *
         * @param id Category ID.
         */
        void onDelete(String id);

        /**
         * Update callback.
         *
         * @param id Category ID.
         */
        void onUpdate(String id);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvCategoryType;
        ImageView ivCategoryDetailIcon;
        ImageButton ibCategoryEdit, ibCategoryDelete;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryType = itemView.findViewById(R.id.tvCategoryType);
            ivCategoryDetailIcon = itemView.findViewById(R.id.ivCategoryDetailIcon);
            ibCategoryEdit = itemView.findViewById(R.id.ibCategoryEdit);
            ibCategoryDelete = itemView.findViewById(R.id.ibCategoryDelete);
        }
    }
}
