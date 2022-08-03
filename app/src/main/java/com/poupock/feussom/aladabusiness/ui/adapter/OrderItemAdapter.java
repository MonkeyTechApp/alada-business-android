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
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.util.OrderItem;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    boolean isGrid;
    Context context;
    List<OrderItem> orderItems;
    DialogCallback callback;

    public OrderItemAdapter(Context context, List<OrderItem> menuItemList, DialogCallback callback){
        this.orderItems = menuItemList;
        this.context = context;
        this.callback = callback;
    }

    public void setMenuItems(List<OrderItem> orderItems){
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(isGrid)
            view = LayoutInflater.from(context).inflate(R.layout.adapter_grid_menu_item, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.adapter_course_order_item, parent, false);

        return new OrderItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {

        OrderItem orderItem = this.orderItems.get(position);

        if(orderItem.getMenuItem() !=null)
         holder.txtName.setText(orderItem.getMenuItem().getTitle());
        else holder.txtName.setText(
            AppDataBase.getInstance(context).menuItemDao().getSpecificMenuItem(orderItem.getMenu_item_id()).getTitle()
        );

        holder.txtQuantity.setText(orderItem.getQuantity()+"");
        holder.txtPrice.setText((orderItem.getPrice() * orderItem.getQuantity())+" "+context.getString(R.string.currency_cfa));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        TextView txtPrice;
        TextView txtQuantity;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
        }
    }
}
