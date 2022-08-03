package com.poupock.feussom.aladabusiness.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.poupock.feussom.aladabusiness.LoginActivity;
import com.poupock.feussom.aladabusiness.R;


public class SlideAdapter extends PagerAdapter implements View.OnClickListener {

    Context context;
    LayoutInflater inflater;


    int[] images = {
        R.drawable.ic__957136,
            R.drawable.ic__briefcase_family,
            R.drawable.ic_vector_savings
    };
    private String tag = SlideAdapter.class.getSimpleName();

    public SlideAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate( R.layout.adapter_slide_onboard,container,false);

        String[] headings = context.getResources().getStringArray(R.array.on_boarding_heading);
        String[] descriptions = context.getResources().getStringArray(R.array.on_boarding_description);

        TextView txtHeading = view.findViewById(R.id.txtHeading);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        TextView txtNext = view.findViewById(R.id.txtNext);

        Button btnGetStarted = view.findViewById(R.id.btnStarted);
        ImageView imgBack = view.findViewById(R.id.imgVector);

        txtHeading.setText(headings[position]);
        txtDescription.setText(descriptions[position]);
        imgBack.setImageResource(images[position]);

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
        txtNext.setOnClickListener(this);

        container.addView(view);
        Log.i(tag,"The instantiation");
        return view;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull View container, int position) {

        return super.instantiateItem(container, position);

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((RelativeLayout)object);
    }

    @Override
    public void onClick(View view) {

    }
}
