package com.poupock.feussom.aladabusiness.ui.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;

import java.util.List;

public class MenuItemCategoryAdapter extends RecyclerView.Adapter<MenuItemCategoryAdapter.MenuItemCategoryViewHolder> {

    Context context;
    List<MenuItemCategory> menuItemCategories;
    ListItemClickCallback callback;
    private static final String TAG = MenuItemCategoryAdapter.class.getSimpleName();

    public MenuItemCategoryAdapter(Context context, List<MenuItemCategory> menuItemCategoryList,
                                   ListItemClickCallback callback){
        this.menuItemCategories = menuItemCategoryList;
        this.context = context;
        this.callback = callback;
        Log.i(TAG,"Adapter built "+menuItemCategoryList.size());
    }

    public void setMenuItemCategories(List<MenuItemCategory> menuItemCategories){
        this.menuItemCategories = menuItemCategories;
    }

    @NonNull
    @Override
    public MenuItemCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_menu_item_category, parent, false);
        return new MenuItemCategoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MenuItemCategoryViewHolder holder, int position) {

        MenuItemCategory menuItemCategory = this.menuItemCategories.get(position);

        holder.chip.setText(menuItemCategory.getName());
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemClickListener(menuItemCategory, false);
            }
        };
        View.OnLongClickListener onLongClickListener = (new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                callback.onItemClickListener(menuItemCategory, true);
                return true;
            }
        });

        holder.chip.setOnClickListener(listener);
        holder.itemView.setOnClickListener(listener);
        holder.chip.setOnLongClickListener(onLongClickListener);
        holder.itemView.setOnLongClickListener(onLongClickListener);
    }

    @Override
    public int getItemCount() {
        return menuItemCategories.size();
    }

    public static class MenuItemCategoryViewHolder extends RecyclerView.ViewHolder{
        Chip chip;

        public MenuItemCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip);
        }
    }
}
