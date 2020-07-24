package lib;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idehpardazanjavan.drivertaxi.ApiInterface;
import com.idehpardazanjavan.drivertaxi.Library;
import com.idehpardazanjavan.drivertaxi.MapsActivity2;
import com.idehpardazanjavan.drivertaxi.PanelActivity;
import com.idehpardazanjavan.drivertaxi.R;
import com.idehpardazanjavan.drivertaxi.ServerData;
import com.idehpardazanjavan.drivertaxi.SocketConnect;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by idehpardazanjavan on 3/18/19.
 */

public class ServiceView
{
    int status;
    Context context;
    SharedPreferences new_request;
    Button btn_status;
    Button btn_set_status;
    String token;
    SharedPreferences sp;
    int send=1;
    RelativeLayout service_box,message_box,map_box;
    TextView service_price_text,total_price,address2,price,user_name,area_name,stop_times;
    public ServiceView(Context context)
    {
        new_request=context.getSharedPreferences("new_request_data",Context.MODE_PRIVATE);
        status=getStatus();
        this.context=context;
        createView();
        sp=context.getSharedPreferences("Taxi_driver_datas",Context.MODE_PRIVATE);
        token=sp.getString("token","null");

        geo();
        btnStatus();
        if(getStatus()>=3)
        {
            show_service_data();
        }
    }


    public void createView()
    {
        btn_set_status=((Activity) context).findViewById(R.id.btn_set_status);
        btn_status=((Activity) context).findViewById(R.id.btn_status);
        service_box=((Activity) context).findViewById(R.id.service_box);
        message_box=((Activity) context).findViewById(R.id.message_box);
        service_price_text=((Activity) context).findViewById(R.id.service_price);
        total_price=((Activity) context).findViewById(R.id.total_price);
        map_box=((Activity) context).findViewById(R.id.map_box);
        address2=((Activity) context).findViewById(R.id.address2);
        price=((Activity) context).findViewById(R.id.price);
        user_name=((Activity) context).findViewById(R.id.user_name2);
        area_name=((Activity) context).findViewById(R.id.area);
        stop_times=((Activity) context).findViewById(R.id.stop_times);
    }
    public void geo()
    {
        ImageView directions=((Activity) context).findViewById(R.id.directions);
        if(directions!=null){
            directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(getStatus()==2)
                    {
                        Uri location=Uri.parse("geo:"+getLocationData("lat1")+","+getLocationData("lng1")+"?q="+getLocationData("lat1")+","+getLocationData("lng1"));
                        Intent map=new Intent(Intent.ACTION_VIEW,location);
                        context.startActivity(map);
                    }
                    else if(getStatus()==3)
                    {
                        Uri location=Uri.parse("geo:"+getLocationData("lat2")+","+getLocationData("lng2")+"?q="+getLocationData("lat2")+","+getLocationData("lng2"));
                        Intent map=new Intent(Intent.ACTION_VIEW,location);
                        context.startActivity(map);
                    }
                }
            });
        }
    }
    public  double getLocationData(String key)
    {
        return  Double.valueOf(new_request.getString(key,"0.0"));
    }
    public int getStatus()
    {
        return new_request.getInt("status",1);
    }
    public void btnStatus()
    {

        if(getLocationData("lat3")!=0.0 && getStatus()==3)
        {
            btn_set_status.setText("به مقصد اول رسیدم");
        }
        else if(getLocationData("lat3")!=0.0 && getStatus()==4)
        {
            btn_set_status.setText("به مقصد دوم رسیدم");
        }
        else if(new_request.getString("going_back","no").equals("ok") && getStatus()==3)
        {
            btn_set_status.setText("رسیدن به مقصد");
        }

        btn_set_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View alert_view= LayoutInflater.from(context).inflate(R.layout.alert,null);
                final Dialog alert=new Dialog(context);
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                TextView title=(TextView)alert_view.findViewById(R.id.title);
                TextView message=(TextView)alert_view.findViewById(R.id.message);
                TextView btn_no=(TextView)alert_view.findViewById(R.id.btn_no);
                TextView btn_ok=(TextView)alert_view.findViewById(R.id.btn_ok);
                if(getLocationData("lat3")!=0.0)
                {
                    if(getStatus()==3)
                    {
                        title.setText("پایان مقصد اول");
                        message.setText("ایا از پایان مقصد اول اطمینان دارید ؟");
                    }
                    else if(getStatus()==4)
                    {
                        if(getServiceData("going_back").equals("no")){
                            title.setText("پایان سفر");
                            message.setText("ایا از پایان سفر اطمینان دارید ؟");
                        }
                        else{
                            title.setText("پایان مقصد دوم");
                            message.setText("ایا از پایان مقصد دوم اطمینان دارید ؟");
                        }
                    }
                    else if (getStatus()==5){
                        title.setText("پایان سفر");
                        message.setText("ایا از پایان سفر اطمینان دارید ؟");
                    }
                }
                else
                {
                    if(getServiceData("going_back").equals("ok") && getStatus()==3){
                        title.setText("رسیدن به مقصد");
                        message.setText("ایا از رسیدن به مقصد اطمینان دارید ؟");
                    }
                    else{
                        title.setText("پایان سفر");
                        message.setText("ایا از پایان سفر اطمینان دارید ؟");
                    }
                }

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        send_status_service();
                        alert.dismiss();
                    }
                });

                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                alert.setContentView(alert_view);
                alert.show();

            }
        });
    }
    public void send_status_service()
    {
        if(send==1){
            send=0;
            status=status+1;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Service.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiInterface apiInterface = retrofit.create(ApiInterface.class);

            Call<ServerData.status_service> call=apiInterface.set_status(getServiceData("service_id"),token,status,getLocationData("driver_lat"),getLocationData("driver_lng"));

            Callback<ServerData.status_service> callback=new Callback<ServerData.status_service>() {
                @Override
                public void onResponse(Response<ServerData.status_service> response, Retrofit retrofit) {

                    if(response.isSuccess())
                    {
                        send=1;
                        Service.updateStatusService(context,status);
                        if(response.body().getStatus().equals("ok"))
                        {
                            if(status==2)
                            {
                                btn_status.setText("مسافر سوار شد");
                                Service.updateStatusService(context,2);
                            }
                            else if(status==3)
                            {
                                Service.updateStatusService(context,3);
                                show_service_data();
                            }
                            else if(status==4)
                            {
                                Service.updateStatusService(context,4);
                                if(getLocationData("lat3")!=0.0)
                                {
                                    address2.setText(getServiceData("address3"));
                                    btn_set_status.setText("به مقصد دوم رسیدم");
                                }
                                else if(getServiceData("going_back").equals("ok"))
                                {
                                    address2.setText(getServiceData("address1"));
                                    btn_set_status.setText("پایان سفر");
                                }
                                else {

                                    int inventory=response.body().getInventory();
                                    sp.edit().putInt("inventory",inventory).commit();
                                    show_view();
                                }
                            }
                            else if(status==5){
                                if(getLocationData("lat3")!=0.0 && getServiceData("going_back").equals("ok"))
                                {
                                    address2.setText(getServiceData("address1"));
                                    btn_set_status.setText("پایان سفر");
                                }
                                else if(getLocationData("lat3")==0.0 && getServiceData("going_back").equals("ok")){
                                    int inventory=response.body().getInventory();
                                    sp.edit().putInt("inventory",inventory).commit();

                                    Service.updateStatusService(context,5);
                                    show_view();
                                }
                            }
                            else if(status==6){
                                int inventory=response.body().getInventory();
                                sp.edit().putInt("inventory",inventory).commit();

                                Service.updateStatusService(context,5);
                                show_view();
                            }
                        }

                    }
                    else {
                        send=1;
                        Toast.makeText(context, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Throwable t)
                {
                    status=status-1;
                    send=1;
                    Toast.makeText(context, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                }
            };
            call.enqueue(callback);
        }
    }
    public void show_view()
    {

        service_box.setVisibility(View.GONE);

        message_box.setVisibility(View.VISIBLE);

        service_price_text.setText(Library.get_price(getServiceData("price")));

        int p2=Integer.valueOf(getServiceData("price"));
        //+SocketConnect.today_price;

        total_price.setText(Library.get_price(String.valueOf(p2)));

        new_request.edit().clear().commit();
    }
    public String getServiceData(String key)
    {
        return  new_request.getString(key,"null");
    }
    public void show_service_data()
    {
        map_box.setVisibility(View.GONE);
        service_box.setVisibility(View.VISIBLE);
        String p_text;

        address2.setText(getServiceData("address2"));
        if(!getServiceData("stop_time").equals("0")){
            stop_times.setText(  "مدت زمان توقف " + getServiceData("stop_time"));
        }
        int p1=Integer.valueOf(getServiceData("price"));
        int p2=new_request.getInt("user_inventory",0);
        if(p2>=p1){
             p_text="پرداخت هزینه سفر توسط مسافر انجام شده";
             price.setTextColor(Color.GREEN);
        }
        else{
             p_text="مبلغ "+Library.get_price(getServiceData("price"))+" به صورت نقد از مسافر دریافت کنید";
            price.setTextColor(Color.parseColor("#f7ad36"));
        }
        price.setText(p_text);

        user_name.setText(new_request.getString("user_name",""));

        String[]  area=getServiceData("address2").split(" ");
        if(area.length>0)
        {

            area_name.setText(area[0]);
        }
    }
    public void set_status()
    {
        View alert_view= LayoutInflater.from(context).inflate(R.layout.alert,null);
        final Dialog alert=new Dialog(context);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        TextView title=(TextView)alert_view.findViewById(R.id.title);
        TextView message=(TextView)alert_view.findViewById(R.id.message);
        TextView btn_no=(TextView)alert_view.findViewById(R.id.btn_no);
        TextView btn_ok=(TextView)alert_view.findViewById(R.id.btn_ok);

        if(getStatus()==1)
        {
            title.setText("هشدار رسیدن");
            message.setText("آیا از ارسال هشدار رسیدن اطمینان دارید ؟ ");
        }
        else if(getStatus()==2)
        {
            title.setText("مسافر سوار شد");
            message.setText("ایا از سوار شدن مسافر اطمینان دارید ؟");
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_status_service();
                alert.dismiss();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
        alert.setContentView(alert_view);
        alert.show();

    }
    public void geo2()
    {
        if(getStatus()==4 && getServiceData("going_back").equals("ok") && getLocationData("lat3")==0.0)
        {
            Uri location=Uri.parse("geo:"+getLocationData("lat1")+","+getLocationData("lng1")+"?q="+getLocationData("lat1")+","+getLocationData("lng1"));
            Intent map=new Intent(Intent.ACTION_VIEW,location);
            context.startActivity(map);
        }
        if(getStatus()==4 && getServiceData("going_back").equals("ok") && getLocationData("lat3")!=0.0)
        {
            Uri location=Uri.parse("geo:"+getLocationData("lat3")+","+getLocationData("lng3")+"?q="+getLocationData("lat3")+","+getLocationData("lng3"));
            Intent map=new Intent(Intent.ACTION_VIEW,location);
            context.startActivity(map);
        }
        else if(getStatus()==5){
            Uri location=Uri.parse("geo:"+getLocationData("lat1")+","+getLocationData("lng1")+"?q="+getLocationData("lat1")+","+getLocationData("lng1"));
            Intent map=new Intent(Intent.ACTION_VIEW,location);
            context.startActivity(map);
        }
        else if (getStatus()==3)
        {
            Uri location=Uri.parse("geo:"+getLocationData("lat2")+","+getLocationData("lng2")+"?q="+getLocationData("lat2")+","+getLocationData("lng2"));
            Intent map=new Intent(Intent.ACTION_VIEW,location);
            context.startActivity(map);
        }
    }
    public void cancelService(String service_id)
    {
        final Dialog dialog=new Dialog(context);
        View dialog_view= LayoutInflater.from(context).inflate(R.layout.cancel_dialog,null);

        TextView btn_true=(TextView)dialog_view.findViewById(R.id.btn_true);
        TextView btn_false=(TextView)dialog_view.findViewById(R.id.btn_false);
        btn_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Service.baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiInterface apiInterface = retrofit.create(ApiInterface.class);

                Call<String> call=apiInterface.cancel_service(getServiceData("service_id"),token,-2);
                Callback<String> callback=new Callback<String>() {
                    @Override
                    public void onResponse(Response<String> response, Retrofit retrofit) {
                        if(response.body().equals("ok")){

                            context.getSharedPreferences("new_request_data",Context.MODE_PRIVATE).edit().clear().commit();
                            Intent refresh=new Intent(context,PanelActivity.class);
                            context.startActivity(refresh);
                        }
                        else if(response.body().equals("error")){
                            Toast.makeText(context, "خطا در اجرای درخواست مجددا تلاش نمایید", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                };
                call.enqueue(callback);
            }
        });
        btn_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialog_view);
        int w=context.getResources().getDisplayMetrics().widthPixels;
        double w2=(90*w)/100;
        w=(int) w2;
        dialog.getWindow().setLayout(w, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();


    }
}
