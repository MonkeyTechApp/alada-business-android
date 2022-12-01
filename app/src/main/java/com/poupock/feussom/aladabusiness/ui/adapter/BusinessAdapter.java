package com.poupock.feussom.aladabusiness.ui.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.core.business.BusinessActivity;
import com.poupock.feussom.aladabusiness.util.Business;

import java.util.List;

@SuppressLint("SetTextI18n")
public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder>{

    private List<Business> businesses;
    private Context context;
    DialogCallback dialogCallback;
    boolean hasActionDialog;

    public BusinessAdapter(Context context, List<Business> businesses){
        this.context = context;
        this.businesses = businesses;
        this.hasActionDialog = false;
        this.dialogCallback = null;
    }

    public void setBusinesss(List<Business> businesses){
        this.businesses = businesses;
    }

    @NonNull
    @Override
    public BusinessAdapter.BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_guest_table, parent, false);
        return new BusinessAdapter.BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessAdapter.BusinessViewHolder holder, int position) {

        Business business = this.businesses.get(position);

        holder.txtName.setText(business.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BusinessActivity.class);
                intent.putExtra(BusinessActivity.ACTIVE_BUSINESS, new Gson().toJson(business));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    public static class BusinessViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        TextView txtSize;

        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtTableName);
            txtSize = itemView.findViewById(R.id.txtTableSeatsNum);
        }
    }
}
