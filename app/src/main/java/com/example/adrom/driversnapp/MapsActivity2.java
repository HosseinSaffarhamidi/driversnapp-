package com.example.adrom.driversnapp;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.MapView;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import lib.ServiceView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MapsActivity2 extends FragmentActivity implements LocationListener {

    String service_id,token;
    SharedPreferences sp;
    String service_address2,service_price,service_user,service_address3;
    Button btn_set_status;
    int status;
    TextView address,address2;
    int send_score=0;
    LocationManager locationManager;
    SharedPreferences new_request;
    ServiceView serviceView;
    BroadcastReceiver broadcastReceiver;
    int send=0;
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
//                setContentView(R.layout.activity_maps2);
//
//                ShowData();
//                setMarker();
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
        serviceView=new ServiceView(MapsActivity2.this);
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                serviceView=new ServiceView(MapsActivity2.this);

            }
        };
        registerReceiver(broadcastReceiver,new IntentFilter("update_service"));
        TextView user_name=(TextView)findViewById(R.id.user_name);
        address=(TextView)findViewById(R.id.address);
        new_request=getSharedPreferences("new_request_data",MODE_PRIVATE);
        service_address2=new_request.getString("address2","");
        service_address3=new_request.getString("address3","");
        service_user=new_request.getString("name","");
        address.setText("مبدا : "+new_request.getString("address1",""));
        status=new_request.getInt("status",1);
        user_name.setText("مسافر : "+new_request.getString("user_name",""));

        if(serviceView.getStatus()>=3)
        {
            serviceView.show_service_data();
        }
        if (CheckGPS()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MapsActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            } else {
                set_location();
            }
        } else
        {
            show_dialog_box();
        }
    }
    public void setMarker()
    {
        MapView mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.getMapAsync(new com.mapbox.mapboxsdk.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap)
            {
                LatLng latLng=new LatLng(serviceView.getLocationData("lat1"),serviceView.getLocationData("lng1"));
                MarkerOptions options=new MarkerOptions().position(latLng)
                        .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.map_marker));

                LatLng sydney2=new LatLng(serviceView.getLocationData("driver_lat"),serviceView.getLocationData("driver_lng"));

                final MarkerOptions markerOptions2 = new MarkerOptions().position(sydney2)
                        .icon(IconFactory.getInstance(getBaseContext()).fromResource(R.drawable.car_icon));

                mapboxMap.addMarker(options);
                mapboxMap.addMarker(markerOptions2);
                mapboxMap.moveCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom(sydney2,16));
            }
        });

    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void set_status(final View view)
    {
        serviceView.set_status();
    }


    public void location(View view)
    {
        serviceView.geo2();

    }
    public void like(View view)
    {
        if(send_score==0)
        {
            send_score=1;
            SocketConnect.get_socket().emit("set_score_user","like",service_id);
        }
    }
    public void dislike(View view)
    {
        if(send_score==0)
        {
            send_score = 1;
            SocketConnect.get_socket().emit("set_score_user","dislike",service_id);
        }
    }

    public void set_location() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 5, this);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, this);
    }
    @Override
    public void onLocationChanged(Location location) {

        if(location.getProvider().equals("gps"))
        {
            //  locationManager.removeUpdates(this);
        }


        // driver_lat=location.getLatitude();
        // driver_lng=location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.location_message);
        LinearLayout btn_true=(LinearLayout) dialog.findViewById(R.id.btn_true);
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
        dialog.show();
    }

    public void home(View view)
    {
        Intent intent=new Intent(MapsActivity2.this,PanelActivity.class);
        startActivity(intent);
    }

    public void call(View view)
    {
        String mobile=new_request.getString("mobile","0");
        if(mobile.equals("0")){
            Toast.makeText(this, "شماره موبایل ثبت نشده است", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent=new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+mobile));
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    public void cancel(View view){

        serviceView.cancelService(service_id);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(broadcastReceiver);
    }


}
