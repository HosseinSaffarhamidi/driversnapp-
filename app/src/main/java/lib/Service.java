package lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.idehpardazanjavan.drivertaxi.MapsActivity;
import com.idehpardazanjavan.drivertaxi.PanelActivity;
import com.idehpardazanjavan.drivertaxi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by idehpardazanjavan on 3/16/19.
 */

public class Service
{

    //http://192.168.43.210:3000/
    //http://31556.ir:3000/
    public static String baseUrl="http://31556.ir:3000/";
    public static boolean setServiceData(JSONObject jsonObject, Context context)
    {
        SharedPreferences new_request=context.getSharedPreferences("new_request_data",Context.MODE_PRIVATE);
        try
        {
            SharedPreferences.Editor editor=new_request.edit();
            editor.putString("price",jsonObject.getString("price"));
            editor.putString("address1",jsonObject.getString("address1"));
            editor.putString("address2",jsonObject.getString("address2"));
            editor.putString("lat1",jsonObject.getString("lat1"));
            editor.putString("lng1",jsonObject.getString("lng1"));
            editor.putString("lat2",jsonObject.getString("lat2"));
            editor.putString("lng2",jsonObject.getString("lng2"));
            if(jsonObject.has("user_name"))
            {
                editor.putString("user_name",jsonObject.getString("user_name"));
            }
            if(jsonObject.has("mobile")){
                editor.putString("mobile",jsonObject.getString("mobile"));
            }
            if(jsonObject.has("User")){
                JSONObject user_data=jsonObject.getJSONObject("User");
                if(user_data.has("inventory")){
                    editor.putInt("user_inventory",user_data.getInt("inventory"));
                }
            }


            if(!jsonObject.getString("lng3").equals("null") && !jsonObject.getString("lat3").equals("null"))
            {
                editor.putString("address3",jsonObject.getString("address3"));
                editor.putString("lat3",jsonObject.getString("lat3"));
                editor.putString("lng3",jsonObject.getString("lng3"));
            }
            if(jsonObject.has("driver_lat") && jsonObject.has("driver_lng"))
            {
                editor.putString("driver_lat",jsonObject.getString("driver_lat"));
                editor.putString("driver_lng",jsonObject.getString("driver_lng"));
            }
            editor.putString("going_back",jsonObject.getString("going_back"));
            editor.putString("stop_time",jsonObject.getString("stop_time"));
            editor.putString("address3",jsonObject.getString("address3"));
            if(jsonObject.has("service_id"))
            {
                editor.putString("service_id",jsonObject.getString("service_id"));
            }
            editor.putInt("status",Integer.valueOf(jsonObject.getString("status")));

            editor.commit();

            return  true;
        }
        catch (JSONException e)
        {
            Log.i("error",e.getMessage().toString());
            return  false;
        }
    }
    public static void updateStatusService(Context context,int status)
    {
        SharedPreferences new_request=context.getSharedPreferences("new_request_data",Context.MODE_PRIVATE);
        new_request.edit().putInt("status",status).commit();
    }
    public static void removeServiceData(Context context)
    {
        SharedPreferences new_request=context.getSharedPreferences("new_request_data",Context.MODE_PRIVATE);
        new_request.edit().clear().commit();
    }
    public static String replace_number(String number)
    {
        number=number.replace("0","۰");
        number=number.replace("1","۱");
        number=number.replace("2","۲");
        number=number.replace("3","۳");
        number=number.replace("4","۴");
        number=number.replace("5","۵");
        number=number.replace("6","۶");
        number=number.replace("7","۷");
        number=number.replace("8","۸");
        number=number.replace("9","۹");
        return  number;
    }
    public static String get_service_status(int s){
        Map<Integer,String> map=new HashMap<Integer,String>();

        map.put(-3,"لغو سفر توسط مسافر");
        map.put(-2,"لغو سفر توسط رانند");
        map.put(-1,"در انتظار راننده");
        map.put(1,"قبول درخواست توسط راننده");
        map.put(2,"رسیدن راننده به مبدا");
        map.put(3,"سوار شدن مسافر");
        map.put(4,"رسیدن به مقصد اول");
        map.put(5,"رسیدن به مقصد دوم");
        return  map.get(s);
    }
}
