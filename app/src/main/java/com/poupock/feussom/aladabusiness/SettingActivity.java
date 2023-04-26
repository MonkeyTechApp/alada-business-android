package com.poupock.feussom.aladabusiness;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.poupock.feussom.aladabusiness.core.auth.AuthActivity;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.ActivitySettingBinding;
import com.poupock.feussom.aladabusiness.util.User;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.contentSetting.btnLogOut.setOnClickListener(this);

        binding.contentSetting.rdbCourseMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    User.storeSaleMode(User.SALE_COURSE_MODE, SettingActivity.this);
                }
            }
        });

        binding.contentSetting.rdbDirectMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    User.storeSaleMode(User.SALE_DIRECT_MODE, SettingActivity.this);
                }
            }
        });


        binding.contentSetting.rdbCourseMode.setChecked(User.getSaleMode(this).equalsIgnoreCase(User.SALE_COURSE_MODE));
        binding.contentSetting.rdbDirectMode.setChecked(User.getSaleMode(this).equalsIgnoreCase(User.SALE_DIRECT_MODE));
    }

    @Override
    public void onClick(View view) {
        if(view == binding.contentSetting.btnLogOut){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            User.disconnectConnectedUser(SettingActivity.this);
                            User.storeToken("", SettingActivity.this);

                            AppDataBase.getInstance(SettingActivity.this).orderItemDao().emptyTable();
                            AppDataBase.getInstance(SettingActivity.this).courseDao().emptyTable();
                            AppDataBase.getInstance(SettingActivity.this).orderDao().emptyTable();
                            AppDataBase.getInstance(SettingActivity.this).menuItemDao().emptyTable();
                            AppDataBase.getInstance(SettingActivity.this).menuItemCategoryDao().emptyTable();
                            AppDataBase.getInstance(SettingActivity.this).guestTableDao().emptyTable();
                            AppDataBase.getInstance(SettingActivity.this).internalPointDao().emptyTable();
                            AppDataBase.getInstance(SettingActivity.this).businessDao().emptyTable();
                            AppDataBase.getInstance(SettingActivity.this).roleDao().emptyTable();
                            AppDataBase.getInstance(SettingActivity.this).userDao().emptyTable();
                            Intent intent = new Intent(SettingActivity.this, AuthActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.confirm_logout)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).show();
        }

    }
}
