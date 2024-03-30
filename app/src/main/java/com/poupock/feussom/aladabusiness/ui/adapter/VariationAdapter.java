package com.poupock.feussom.aladabusiness.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.util.Variation;

import java.util.List;

public class VariationAdapter extends RecyclerView.Adapter<VariationAdapter.VariationViewHolder> {

    Context context;
    List<Variation> data;
    ListItemClickCallback callback;
    private String tag = VariationAdapter.class.getSimpleName();

    public VariationAdapter(Context context, List<Variation> data, ListItemClickCallback callback){
        this.data = data;
        this.context = context;
        this.callback = callback;
    }
    public void setData(List<Variation> variations){
        this.data = variations;
    }

    public List<Variation> getVariation(){
        return data;
    }



    @NonNull
    @Override
    public VariationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_variation, parent, false);

        return new VariationViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VariationViewHolder holder, int position) {

        Variation datum = this.data.get(position);
        Log.i(tag,  "The menu is : "+new Gson().toJson(datum));


        holder.txtPrice.setText((datum.getItemPrice()+datum.getPrice_adjustment())+" "+context.getString(R.string.currency_cfa));
        holder.txtName.setText(context.getString(R.string.size)+" : "+datum.getSize());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemClickListener(datum, false);
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
        return data.size();
    }

    public static class VariationViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        TextView txtPrice;

        public VariationViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}
