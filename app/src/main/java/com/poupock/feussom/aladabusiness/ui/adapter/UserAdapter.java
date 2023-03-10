package com.poupock.feussom.aladabusiness.ui.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.core.business.BusinessActivity;
import com.poupock.feussom.aladabusiness.util.User;

import java.util.List;

@SuppressLint("SetTextI18n")
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.BusinessViewHolder>{

    private List<User> users;
    private Context context;
    DialogCallback dialogCallback;
    boolean hasActionDialog;

    public UserAdapter(Context context, List<User> users, boolean hasActionDialog, DialogCallback callback){
        this.context = context;
        this.users = users;
        this.hasActionDialog = hasActionDialog;
        this.dialogCallback = callback;
    }

    public void setUsers(List<User> users){
        this.users = users;
    }

    @NonNull
    @Override
    public UserAdapter.BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_user, parent, false);
        return new UserAdapter.BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.BusinessViewHolder holder, int position) {

        User datum = this.users.get(position);

        String[] arr = context.getResources().getStringArray(R.array.role_array);
        holder.txtName.setText(datum.getName());
        holder.txtRole.setText(arr[Integer.parseInt(datum.getRole_id())-1]+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasActionDialog){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.actions)
                            .setCancelable(true)
                            .setItems(R.array.action_array, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    dialogCallback.onActionClicked(datum, which);
                                }
                            }).create().show();
                }
                else {
//                    dialogCallback.onItemClickListener(guestTable, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class BusinessViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        TextView txtRole;
        ImageView imgUser;

        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtRole = itemView.findViewById(R.id.txtRole);
            imgUser = itemView.findViewById(R.id.imgLogo);
        }
    }
}
