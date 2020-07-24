package com.example.adrom.driversnapp;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import android.provider.Settings;

import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DecimalFormat;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import lib.Service;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PanelActivity extends AppCompatActivity {

    TextView gps_status;
    Intent Location;
    Handler handler;
    SharedPreferences sp;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranSansWeb.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        Intent myService=new Intent(this,MyService.class);
        startService(myService);

        Library.startAlarmManager(this);

        handler=new Handler();

        gps_status=(TextView)findViewById(R.id.gps_status);

        sp=getSharedPreferences("Taxi_driver_datas",MODE_PRIVATE);
        String token=sp.getString("token","null");






        Location=new Intent(this,LocationService.class);
        if(CheckGPS()){
            gps_status.setText("فعال");
            request_location();
        }
        else{
            gps_status.setText("غیر فعال");
            gps_status.setTextColor(Color.RED);
            show_dialog_box();
        }

        show_driver_request_status();
        set_inventory();

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onBackPressed() {

        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
    public void change_status1(View view){
        set_request_status(1);
    }
    public void change_status2(View view){
        set_request_status(2);
    }
    public void set_request_status(final int type){


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Service.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<String> call=apiInterface.set_request_status(Library.getToken(PanelActivity.this),type);

        Callback<String> callback=new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    if(response.body().equals("ok"))
                    {

                        if (type==1){
                            sp.edit().putString("driving_request_status","ok").commit();
                        }
                        else{
                            sp.edit().putString("driving_request_status","no").commit();
                        }
                        show_driver_request_status();
                    }
                    else{
                        Toast.makeText(PanelActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    getSharedPreferences("Taxi_driver_datas",MODE_PRIVATE).edit().clear().commit();
                    Intent login=new Intent(PanelActivity.this, com.example.adrom.driversnapp.login.class);
                    startActivity(login);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(PanelActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            }
        };

        call.enqueue(callback);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==101){
            startService(Location);
        }
    }
    public boolean CheckGPS()
    {
        boolean status;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        status=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return  status;
    }
    public  void show_dialog_box()
    {
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.location_message);

        LinearLayout btn_true=(LinearLayout)dialog.findViewById(R.id.btn_true);
        btn_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent j=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(j);
            }
        });

        LinearLayout btn_false=(LinearLayout)dialog.findViewById(R.id.btn_false);
        btn_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        if(!dialog.isShowing())
        {
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CheckGPS()){
            gps_status.setText("فعال");
            gps_status.setTextColor(getResources().getColor(R.color.btn_color));

            if(dialog!=null){
                dialog.dismiss();
            }
            request_location();

        }
    }
    public void request_location(){
        if (ActivityCompat.checkSelfPermission(PanelActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PanelActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PanelActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        else{
            startService(Location);
        }
    }
    public void show_driver_request_status(){
        View available=(View)findViewById(R.id.available);
        View unavailable=(View)findViewById(R.id.unavailable);
        if(sp.getString("driving_request_status","no").equals("ok")){
            unavailable.setBackgroundColor(getResources().getColor(R.color.inActive));
            available.setBackgroundColor(getResources().getColor(R.color.available));
        }
        else{
            unavailable.setBackgroundColor(getResources().getColor(R.color.unavailable));
            available.setBackgroundColor(getResources().getColor(R.color.inActive));
        }
    }
    public void set_inventory(){
        TextView inventory=(TextView)findViewById(R.id.inventory);
        int price=sp.getInt("inventory",0);

        DecimalFormat decimalFormat=new DecimalFormat();
        String p_string=decimalFormat.format(price)+" ریال";
        p_string=Service.replace_number(p_string);

        inventory.setText(p_string);
    }
    public void show_service(View view)
    {
        Intent intent=new Intent(this,ServiceListActivity.class);
        startActivity(intent);
    }
}

