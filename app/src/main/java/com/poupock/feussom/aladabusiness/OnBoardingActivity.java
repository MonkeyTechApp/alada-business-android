package com.poupock.feussom.aladabusiness;

import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.poupock.feussom.aladabusiness.ui.adapter.SlideAdapter;

public class OnBoardingActivity extends AppCompatActivity {

    private ViewPager viewPager; // Viewpager which use is to enable sliding effect.
    private LinearLayout dotLayout; // Container layout which will store the indicators.
    private TextView[] dotLists; // List of dots to indicate pager position.

    SlideAdapter slideAdapter; // Adapter which displays the different slides to the user.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AlAdaBusiness);
        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.viewPager);
        dotLayout = findViewById(R.id.dotLayout);

        slideAdapter = new SlideAdapter(this);

        viewPager.setAdapter(slideAdapter);

        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(pageChangeListener);
    }


    public void addDotsIndicator(int position){
        dotLists = new TextView[3];
        dotLayout.removeAllViews();

        for (int  i = 0 ; i < dotLists.length ; i ++){
            dotLists[i] = new TextView(this);
            dotLists[i].setText(Html.fromHtml("&#8226"));
            dotLists[i].setTextSize(35);
            dotLists[i].setTextColor(getResources().getColor(R.color.grey));

            dotLayout.addView(dotLists[i]);
        }

        if(dotLists.length > 0){
            dotLists[position].setTextColor(getResources().getColor(R.color.action_bar_color));
        }
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };


}
