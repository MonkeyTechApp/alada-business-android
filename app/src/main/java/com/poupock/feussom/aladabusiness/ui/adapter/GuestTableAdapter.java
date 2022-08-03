package com.poupock.feussom.aladabusiness.ui.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.MenuItem;

import java.util.List;

@SuppressLint("SetTextI18n")
public class GuestTableAdapter extends RecyclerView.Adapter<GuestTableAdapter.GuestTableViewHolder>{

    private List<GuestTable> guestTables;
    private Context context;
    DialogCallback dialogCallback;
    ListItemClickCallback itemClickCallback;
    boolean hasActionDialog;

    public GuestTableAdapter(Context context, List<GuestTable> guestTables, DialogCallback callback){
        this.context = context;
        this.guestTables = guestTables;
        this.dialogCallback = callback;
        this.hasActionDialog = true;
        this.itemClickCallback = null;
    }

    public GuestTableAdapter(Context context, List<GuestTable> guestTables, ListItemClickCallback callback){
        this.context = context;
        this.guestTables = guestTables;
        this.itemClickCallback = callback;
        this.hasActionDialog = false;
        this.dialogCallback = null;
    }

    public void setGuestTables(List<GuestTable> guestTables){
        this.guestTables = guestTables;
    }

    @NonNull
    @Override
    public GuestTableAdapter.GuestTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_guest_table, parent, false);
        return new GuestTableAdapter.GuestTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestTableAdapter.GuestTableViewHolder holder, int position) {

        GuestTable guestTable = this.guestTables.get(position);

        holder.txtSize.setText(guestTable.getCapacity()+" "+((guestTable.getCapacity()>1) ? context.getString(R.string.seats) : context.getString(R.string.seat)));
        holder.txtName.setText(guestTable.getTitle());

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
                                dialogCallback.onActionClicked(guestTable, which);
                            }
                        }).create().show();
                }
                else {
                    itemClickCallback.onItemClickListener(guestTable, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return guestTables.size();
    }

    public static class GuestTableViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        TextView txtSize;

        public GuestTableViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtTableName);
            txtSize = itemView.findViewById(R.id.txtTableSeatsNum);
        }
    }
}
