package com.poupock.feussom.aladabusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.poupock.feussom.aladabusiness.core.restaurant.BusinessCreationActivity;
import com.poupock.feussom.aladabusiness.core.table.TableActivity;
import com.poupock.feussom.aladabusiness.database.AppDataBase;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(AppDataBase.getInstance(LoginActivity.this).businessDao().getAllBusinesses().isEmpty()){
                    intent = new Intent(LoginActivity.this, BusinessCreationActivity.class);
                }else{
                    intent = new Intent(LoginActivity.this, HomeActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
