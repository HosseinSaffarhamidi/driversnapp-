package com.example.adrom.driversnapp;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import androidx.annotation.Nullable;


/**
 * Created by idehpardazanjavan on 10/29/18.
 */

public class MyService extends Service
{

    Handler handler;
    Socket socket;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {

        handler=new Handler();
        try {


            //http://192.168.1.117:5000
            String token=Library.getToken(MyService.this);
            IO.Options options=new IO.Options();
            options.query="group=driver&token="+token;
            socket = IO.socket(lib.Service.baseUrl,options);
            socket.connect();
            SocketConnect.set_Socket(socket);
            socket.on("notification",notificationHandle);
            socket.on("update_service",updateServiceHandle);
            socket.on("cancel_service",CancelServiceHandle);
            socket.emit("request_service",token);
            socket.on("request",driveringRequest);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    public Emitter.Listener notificationHandle=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            final JSONObject data=(JSONObject)args[0];
            handler.post(new Runnable() {
                @Override
                public void run() {

                    try {

                        String title=data.getString("title");
                        String content=data.getString("content");
                        String activity_key=data.getString("activity_key");
                        String activity_value=data.getString("activity_value");

                        String class_name="com.idehpardazanjavan.drivertaxi.MainActivity";

                        if(!data.getString("activity").equals("null"))
                        {
                            class_name=data.getString("activity");
                        }

                        Library.show_notification(MyService.this,title,content,class_name,activity_key,activity_value);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public Emitter.Listener updateServiceHandle=new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    lib.Service.setServiceData(jsonObject,MyService.this);
                    Intent intent=new Intent("update_service");
                    sendBroadcast(intent);
                }
            });
        }
    };

    public Emitter.Listener CancelServiceHandle = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyService.this, "سفارش شما توسط مسافر لغو شد", Toast.LENGTH_SHORT).show();

                    Intent refresh=new Intent(MyService.this,PanelActivity.class);
                    startActivity(refresh);

                    getSharedPreferences("new_request_data", Context.MODE_PRIVATE).edit().clear().commit();

                }
            });
        }
    };

    public class UserLocation implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location) {

            Toast.makeText(MyService.this, String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
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
    }
    public Emitter.Listener driveringRequest=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            handler.post(new Runnable() {
                @Override
                public void run() {


                    JSONObject jsonObject=(JSONObject)args[0];
                    if(lib.Service.setServiceData(jsonObject,MyService.this)){

                        //new code
                        Intent j=new Intent(MyService.this,MapsActivity.class);
                        j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(j);

                    }

                }
            });
        }
    };
}
