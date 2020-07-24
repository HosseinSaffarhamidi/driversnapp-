package com.example.adrom.driversnapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.MapView;


import com.google.android.gms.maps.GoogleMap;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;


import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import lib.Service;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MapsActivity extends FragmentActivity {
    private GoogleMap mMap;
    Handler handler;
    int i=0;
    double lat1,lat2,lat3;
    double lng1,lng2,lng3;
    double driver_lat;
    double driver_lng;
    SharedPreferences new_request;
    Thread thread;
    String address1,address2,address3,service_id,stop_time,going_back,price,token;
    SharedPreferences sp;
    String p_string;
    int send=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranSansWeb.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        CedarMaps.getInstance()
                .setClientID(CedarMapConfig.CLIENT_ID)
                .setClientSecret(CedarMapConfig.CLIENT_SECRET)
                .setContext(this);

//        CedarMaps.getInstance().prepareTiles(new OnTilesConfigured() {
//            @Override
//            public void onSuccess() {
//                setContentView(R.layout.activity_maps);
//
//                ShowData();
//                addMarker();
//            }
//
//            @Override
//            public void onFailure(@NonNull String errorMessage) {
//
//            }
//        });



    }

    public void ShowData()
    {
        new_request=getSharedPreferences("new_request_data",MODE_PRIVATE);

        final DecimalFormat decimalFormat=new DecimalFormat("###,###");

        final ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);

        Bundle bundle=getIntent().getExtras();
        price=new_request.getString("price","");
        price=new_request.getString("price","");
        address1=new_request.getString("address1","");
        address2=new_request.getString("address2","");
        address3=new_request.getString("address3","");
        lat1=Double.valueOf(new_request.getString("lat1","0.0"));
        lng1=Double.valueOf(new_request.getString("lng1","0.0"));
        lat2=Double.valueOf(new_request.getString("lat2","0.0"));
        lng2=Double.valueOf(new_request.getString("lng2","0.0"));
        driver_lat=Double.valueOf(new_request.getString("driver_lat","0.0"));
        driver_lng=Double.valueOf(new_request.getString("driver_lng","0.0"));
        going_back=new_request.getString("going_back","no");
        stop_time=new_request.getString("stop_time","0");
        service_id=new_request.getString("service_id","");

        Intent intent=getIntent();
        if(intent.hasExtra("lat3") && intent.hasExtra("lng3"))
        {
            lat3=bundle.getDouble("lat3");
            lng3=bundle.getDouble("lng3");
            address3=bundle.getString("address3");
        }


        TextView price_text=(TextView)findViewById(R.id.price);
        TextView address1_text=(TextView)findViewById(R.id.address1);
        TextView address2_text=(TextView)findViewById(R.id.address2);
        TextView address3_text=(TextView)findViewById(R.id.address3);
        TextView going_back_text=(TextView)findViewById(R.id.going_back);
        TextView stop_time_text=(TextView)findViewById(R.id.stop_time);

        address1_text.setText(address1);
        address2_text.setText(address2);


        sp=getSharedPreferences("Taxi_driver_datas",MODE_PRIVATE);
        token=sp.getString("token","null");

        if(!address3.equals("null") && !address3.isEmpty())
        {
            RelativeLayout layout_address3=(RelativeLayout)findViewById(R.id.layout_address3);
            layout_address3.setVisibility(View.VISIBLE);
            address3_text.setText(address3);
        }

        if(going_back.equals("ok"))
        {
            going_back_text.setText("دو طرفه");
        }

        if(!stop_time.equals("0"))
        {
            stop_time_text.setText(stop_time);
        }



        p_string=Service.replace_number(p_string);

        price_text.setText(p_string);

        handler=new Handler();

        thread = new Thread() {
            @Override
            public void run() {
                while (i<40)
                {
                    i+=1;
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                progressBar.setProgress(i);
                            }
                        });

                        Thread.sleep(1000);
                        if(i==40){
                            Intent j=new Intent(MapsActivity.this,PanelActivity.class);
                            startActivity(j);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();

    }

    public void addMarker()
    {
        MapView mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.getMapAsync(new com.mapbox.mapboxsdk.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                LatLng latLng=new LatLng(lat1, lng1);
                MarkerOptions options=new MarkerOptions().position(latLng)
                        .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.map_marker));

                LatLng sydney2=new LatLng(driver_lat,driver_lng);

                MarkerOptions options2=new MarkerOptions().position(sydney2)
                        .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.car_icon));

                mapboxMap.addMarker(options);
                mapboxMap.addMarker(options2);

                mapboxMap.moveCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom(sydney2,16));
            }
        });
    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    public void get_request(View view)
    {
        if(send==1)
        {
            send=0;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Service.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiInterface apiInterface = retrofit.create(ApiInterface.class);

            Call<ServerData.request_service> call=apiInterface.set_request(service_id,token,driver_lat,driver_lng);

            Callback<ServerData.request_service> callback= new Callback<ServerData.request_service>() {
                @Override
                public void onResponse(Call<ServerData.request_service> call, Response<ServerData.request_service> response) {
                    if(response.isSuccessful())
                    {
                        if(response.body().status.equals("ok"))
                        {
                            i=100;
                            SharedPreferences.Editor editor=new_request.edit();
                            editor.putInt("status",1);
                            editor.putString("mobile",response.body().getMobile());
                            editor.putInt("user_inventory",response.body().user_inventory);
                            editor.commit();
                            Intent j=new Intent(MapsActivity.this,MapsActivity2.class);
                            startActivity(j);
                        }
                        else if(response.body().status.equals("cancel_service"))
                        {
                            Toast.makeText(MapsActivity.this, "درخواست توسط کاربر لغو شد", Toast.LENGTH_SHORT).show();
                            Intent j=new Intent(MapsActivity.this,PanelActivity.class);
                            startActivity(j);
                        }
                        else{
                            Toast.makeText(MapsActivity.this, "درخواست توسط راننده دیگری قبول شد", Toast.LENGTH_SHORT).show();
                            Intent j=new Intent(MapsActivity.this,PanelActivity.class);
                            startActivity(j);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerData.request_service> call, Throwable t) {
                    send=1;
                    Toast.makeText(MapsActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                }
            };

            call.enqueue(callback);
        }
    }



}
