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
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    boolean isGrid;
    boolean showAll;
    Context context;
    List<Order> orders;
    ListItemClickCallback callback;

    public OrderAdapter(Context context, List<Order> orderList, boolean showAll, ListItemClickCallback callback){
        this.orders = orderList;
        this.context = context;
        this.showAll = showAll;
        this.callback = callback;
    }

    public void setOrders(List<Order> orders){
        this.orders = orders;
    }

    public List<Order> getOrders(){ return this.orders; }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(isGrid)
            view = LayoutInflater.from(context).inflate(R.layout.adapter_grid_menu_item, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.adapter_order, parent, false);

        return new OrderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        Order order = this.orders.get(position);

        holder.txtCode.setText(order.getCode());
        holder.txtTime.setText(Methods.formatTime(order.getCreated_at()));
        AppDataBase.getInstance(context).courseDao().getOrderCourses(order.getId()).size();
        List<Course> courses = AppDataBase.getInstance(context).orderDao().getOrderWithCourseList(order.getId()).courses;
        if (courses != null){
            if (!courses.isEmpty()){
                order.setCourseList(courses);
                for (int j=0; j<courses.size(); j++){
                    List<OrderItem> orderItems = AppDataBase.getInstance(context).orderItemDao().getAllCourseItems(courses.get(j).getId());
                    if (orderItems != null){
                        if (!orderItems.isEmpty()){
                            courses.get(j).setOrderItems(orderItems);
                        }
                    }
                }
            }
        }

        if(showAll){
            holder.txtTable.setText(AppDataBase.getInstance(context).guestTableDao().getSpecificGuestTable(order.getGuest_table_id()).getTitle());
            holder.txtStatus.setText(Methods.processStatus(order.getStatus()));
            holder.txtAmount.setText(order.getTotal() +" "+ context.getString(R.string.currency_cfa));
        }
        else {
            holder.txtTable.setVisibility(View.GONE);
            holder.txtStatus.setVisibility(View.GONE);
            holder.txtAmount.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemClickListener(order, false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{
        TextView txtCode;
        TextView txtAmount;
        TextView txtStatus;
        TextView txtTime;
        TextView txtTable;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtCode = itemView.findViewById(R.id.txtCode);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTable = itemView.findViewById(R.id.txtTable);
        }
    }
}
