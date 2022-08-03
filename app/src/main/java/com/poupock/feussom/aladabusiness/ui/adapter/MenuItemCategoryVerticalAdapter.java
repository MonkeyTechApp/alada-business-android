
package com.poupock.feussom.aladabusiness.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;

import java.util.List;

public class MenuItemCategoryVerticalAdapter extends RecyclerView.Adapter<MenuItemCategoryVerticalAdapter.MenuItemCategoryViewHolder> {

    Context context;
    List<MenuItemCategory> menuItemCategories;
    ListItemClickCallback listItemClickCallback;

    public MenuItemCategoryVerticalAdapter(Context context, List<MenuItemCategory> menuItemCategoryList, ListItemClickCallback listItemClickCallback){
        this.menuItemCategories = menuItemCategoryList;
        this.context = context;
        this.listItemClickCallback = listItemClickCallback;
    }

    public void setMenuItemCategories(List<MenuItemCategory> menuItemCategories){
        this.menuItemCategories = menuItemCategories;
    }

    @NonNull
    @Override
    public MenuItemCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_menu_item_category_vertical, parent, false);
        return new MenuItemCategoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MenuItemCategoryViewHolder holder, int position) {

        MenuItemCategory menuItemCategory = this.menuItemCategories.get(position);

        holder.textView.setText(menuItemCategory.getName());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItemClickCallback.onItemClickListener(menuItemCategory, false);
            }
        };

        holder.textView.setOnClickListener(listener);
        holder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return menuItemCategories.size();
    }

    public static class MenuItemCategoryViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public MenuItemCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.txtName);
        }
    }
}
