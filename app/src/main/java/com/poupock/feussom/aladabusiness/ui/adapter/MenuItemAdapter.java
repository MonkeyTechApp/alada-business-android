package com.poupock.feussom.aladabusiness.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.util.MenuItem;

import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {

    boolean isGrid;
    Context context;
    List<MenuItem> menuItems;
    ListItemClickCallback callback;

    public MenuItemAdapter(Context context, List<MenuItem> menuItemList, ListItemClickCallback callback){
        this.isGrid = false;
        this.menuItems = menuItemList;
        this.context = context;
        this.callback = callback;
    }
    public MenuItemAdapter(Context context, List<MenuItem> menuItemList, boolean isGrid, ListItemClickCallback callback){
        this.isGrid = isGrid;
        this.menuItems = menuItemList;
        this.context = context;
        this.callback = callback;
    }

    public void setMenuItems(List<MenuItem> menuItems){
        this.menuItems = menuItems;
    }

    public List<MenuItem> getMenuItems(){
        return menuItems;
    }



    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(isGrid)
            view = LayoutInflater.from(context).inflate(R.layout.adapter_grid_menu_item, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.adapter_menu_item, parent, false);

        return new MenuItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {

        MenuItem menuItem = this.menuItems.get(position);

        holder.txtPrice.setText(menuItem.getPrice()+" "+context.getString(R.string.currency_cfa));
        holder.txtName.setText(menuItem.getTitle());
        if(!isGrid)
        {
            if(menuItem.getMenuItemCategory()!= null)
                holder.txtCategory.setText(menuItem.getMenuItemCategory().getName());
            else holder.txtCategory.setText(AppDataBase.getInstance(context).menuItemCategoryDao().getSpecificMenuItemCategory(
                menuItem.getMenu_item_category_id()).getName()+"");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemClickListener(menuItem, false);
            }
        });

//        if(menuItem.getPic_local_path() != null)
//        {
//            File imageFile = new File(menuItem.getPic_local_path());
//
//            if (imageFile.exists()){
//                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//                holder.imgProduct.setImageBitmap(bitmap);
//            }
//        }

    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class MenuItemViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        TextView txtPrice;
        TextView txtCategory;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtCategory = itemView.findViewById(R.id.txtCategory);
        }
    }
}
