package com.poupock.feussom.aladabusiness.ui.adapter;

import android.content.Context;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epson.epos2.discovery.DeviceInfo;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.callback.PrinterSelectedCallback;
import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.PrinterDevice;

import java.util.List;

public class PrinterRecycleAdapter extends RecyclerView.Adapter<PrinterRecycleAdapter.PrinterViewHolder>{

    Context context;
    List<DeviceInfo> printers;
    PrinterSelectedCallback callback;

    public PrinterRecycleAdapter(Context context, List<DeviceInfo> printers, PrinterSelectedCallback callback){
        this.context = context;
        this.printers = printers;
        this.callback = callback;
    }

    public void setPrinters(List<DeviceInfo> printers){
        this.printers = printers;
    }

    @NonNull
    @Override
    public PrinterRecycleAdapter.PrinterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_print_device, parent, false);
        return new PrinterRecycleAdapter.PrinterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrinterRecycleAdapter.PrinterViewHolder holder, int position) {

        DeviceInfo printerDevice = this.printers.get(position);

        holder.txtTarget.setText(printerDevice.getTarget());
        holder.txtName.setText(printerDevice.getDeviceName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemClickListener(printerDevice, false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return printers.size();
    }

    public static class PrinterViewHolder extends RecyclerView.ViewHolder{
        TextView txtTarget;
        TextView txtName;

        public PrinterViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtTarget = itemView.findViewById(R.id.txtTarget);
        }
    }
}
