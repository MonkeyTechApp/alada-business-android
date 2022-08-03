package com.poupock.feussom.aladabusiness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.poupock.feussom.aladabusiness.core.menu.MenuItemActivity;
import com.poupock.feussom.aladabusiness.core.table.TableActivity;
import com.poupock.feussom.aladabusiness.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.contentSetting.btnFloorPlan.setOnClickListener(this);
        binding.contentSetting.btnMenu.setOnClickListener(this);
        binding.contentSetting.btnRestaurant.setOnClickListener(this);
        binding.contentSetting.btnUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == binding.contentSetting.btnMenu){
            startActivity(new Intent(this, MenuItemActivity.class));
        }
        else if(view == binding.contentSetting.btnFloorPlan){
            startActivity(new Intent(this, TableActivity.class));
        }
    }
}
