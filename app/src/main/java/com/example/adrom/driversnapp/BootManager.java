package com.example.adrom.driversnapp;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by Hossein Saffarhamidi.
 */

public class BootManager  extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(!checkServiceRunning(context))
        {
            Intent myService=new Intent(context,MyService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(myService);
            }
            else {
                context.startService(myService);
            }

            Library.startAlarmManager(context);
        }

    }
    public boolean checkServiceRunning(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if ("com.idehpardazanjavan.drivertaxi.MyService"
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
}
