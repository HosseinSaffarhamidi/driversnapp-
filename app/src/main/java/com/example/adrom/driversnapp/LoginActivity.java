package com.example.adrom.driversnapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;




public class LoginActivity extends AppCompatActivity {
    SharedPreferences sp;
    private int show_password = 0;
    String baseUrl = "http://192.168.1.117:5000/";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranSansWeb.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    public void login_driver( View view ){
        final EditText mobile = (EditText)findViewById(R.id.mobile);
        final EditText password = (EditText)findViewById(R.id.password);

       password.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               if(motionEvent.getAction()==motionEvent.ACTION_UP)
               {
                   if((motionEvent.getRawX()-password.getCompoundDrawables()[0].getBounds().width())<=password.getCompoundDrawables()[0].getBounds().width())
                   {
                       if(show_password==0)
                       {
                           show_password=1;
                           password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.eye,0);
                           password.setInputType(InputType.TYPE_CLASS_TEXT);
                       }
                       else
                       {
                           show_password=0;
                           password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.eye_off,0);
                           password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                       }
                   }

               }
               return false;
           }
       });

        if( mobile.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty() ){
            Toast.makeText(this,"اطلاعات لازم برای ورود را وارد نمایید.",Toast.LENGTH_LONG).show();
        }else{

//            OkHttpClient.Builder client = new OkHttpClient.Builder();
//            client.connectTimeout(15, TimeUnit.SECONDS);
//            client.readTimeout(15, TimeUnit.SECONDS);
//            client.writeTimeout(15, TimeUnit.SECONDS);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(client.build())
                    .build();

            ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            Call<ServerData.login> call = apiInterface.login(mobile.getText().toString(), password.getText().toString());


            Callback<ServerData.login> callback = new Callback<ServerData.login>() {
                @Override
                public void onResponse(Call<ServerData.login> call, Response<ServerData.login> response) {
                    if (response.isSuccessful())
                    {
                        if(response.body().auth.equals("true"))
                        {
                            String token=response.body().token;
                            sp=getSharedPreferences("Taxi_driver_datas",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("token",token);
//                            editor.putInt("inventory",response.body().getInventory());
                            editor.commit();

                            Intent j=new Intent(LoginActivity.this,PanelActivity.class);
                            startActivity(j);

                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"شماره موبایل یا کلمه عبور وارد شده اشتباه می باشد",Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerData.login> call, Throwable t) {
                    Log.i("Adrom: ",t.toString());
                    Toast.makeText(LoginActivity.this,"خطا در برقراری ارتباط با سرور",Toast.LENGTH_LONG).show();
                }
            };

            call.enqueue(callback);
        }
    }

}
