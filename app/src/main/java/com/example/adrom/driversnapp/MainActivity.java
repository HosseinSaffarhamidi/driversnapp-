package com.example.adrom.driversnapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences new_request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranSansWeb.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_main);



        new_request=getSharedPreferences("new_request_data",MODE_PRIVATE);

        sp=getSharedPreferences("Taxi_driver_datas",MODE_PRIVATE);
        String token=sp.getString("token","no");
        if(!token.equals("no"))
        {
            if(new_request.getInt("status",-50)>=1)
            {
                Intent map=new Intent(MainActivity.this,MapsActivity2.class);
                startActivity(map);
            }
            else
            {
                if(new_request.getInt("status",100)==0)
                {
                    new_request.edit().clear().commit();
                }
                Intent j=new Intent(this,PanelActivity.class);
                startActivity(j);
            }
        }
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    public void show_login_activity(View view)
    {
        Intent j=new Intent(MainActivity.this,login.class);
        startActivity(j);
    }
}


