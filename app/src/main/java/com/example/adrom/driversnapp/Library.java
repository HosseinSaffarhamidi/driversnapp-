package com.example.adrom.driversnapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

import android.widget.RemoteViews;

import java.text.DecimalFormat;

import androidx.core.app.NotificationCompat;


/**
 * Created by Hossein Saffarhamidi.
 */

public class Library
{
    static int i=0;
    public static String get_price(String price)
    {
        DecimalFormat decimalFormat=new DecimalFormat("###,###");

        String p_string=decimalFormat.format(Integer.valueOf(price))+" ریال";

        p_string=p_string.replace("0","۰");
        p_string=p_string.replace("1","۱");
        p_string=p_string.replace("2","۲");
        p_string=p_string.replace("3","۳");
        p_string=p_string.replace("4","۴");
        p_string=p_string.replace("5","۵");
        p_string=p_string.replace("6","۶");
        p_string=p_string.replace("7","۷");
        p_string=p_string.replace("8","۸");
        p_string=p_string.replace("9","۹");

        return  p_string;
    }
    public static void show_notification(Context context,String title,String text,String class_name,String activity_key,String activity_value)
    {
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId="user_channel";
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,channelId);
        builder.setSmallIcon(R.mipmap.notification_logo);
        // builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_logo));
        // builder.setContentTitle("تاکسی");
        //builder.setContentText("تاکسی شما رسید");
        builder.setColor(Color.RED);


        RemoteViews content=new RemoteViews(context.getPackageName(),R.layout.notification);
        content.setTextViewText(R.id.title,title);
        content.setTextViewText(R.id.content,text);
        builder.setCustomContentView(content);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            String channel_name="taxi notification";
            int importance=NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel=new NotificationChannel(channelId,channel_name,importance);
            notificationChannel.setDescription("taxi notification channel");
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            if(notificationManager!=null)
            {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }


        Class<?> myClass=null;

        try {
            myClass=myClass.forName(class_name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent=new Intent(context,myClass);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        i++;
        notificationManager.notify(i,builder.build());
    }
    public static String getToken(Context context)
    {
        SharedPreferences sp=context.getSharedPreferences("Taxi_driver_datas",Context.MODE_PRIVATE);
        return  sp.getString("token","null");
    }
    public static int pxfromdp(Context context, int dp)
    {
        float a=dp*context.getResources().getDisplayMetrics().density;
        return (int)a;
    }
    public static void startAlarmManager(Context context)
    {
        Intent alarmIntent=new Intent(context,AlarmManager.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,0,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        android.app.AlarmManager alarmManager=(android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(android.app.AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60*1000,pendingIntent);

    }
}
