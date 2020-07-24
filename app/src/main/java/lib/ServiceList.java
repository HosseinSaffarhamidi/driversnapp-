package lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.Dimension;
import com.cedarstudios.cedarmapssdk.listeners.StaticMapImageResultListener;
import com.idehpardazanjavan.drivertaxi.ApiInterface;
import com.idehpardazanjavan.drivertaxi.CedarMapConfig;
import com.idehpardazanjavan.drivertaxi.Library;
import com.idehpardazanjavan.drivertaxi.R;
import com.idehpardazanjavan.drivertaxi.ServerData;
import com.idehpardazanjavan.drivertaxi.ServiceActivity;
import com.idehpardazanjavan.drivertaxi.ServiceListActivity;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

import lib.Service;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by idehpardazanjavan on 6/2/19.
 */

public class ServiceList
{
    public int load=1;
    Context context;
    int page=1;
    public ServiceList(Context context){

        CedarMaps.getInstance()
                .setClientID(CedarMapConfig.CLIENT_ID)
                .setClientSecret(CedarMapConfig.CLIENT_SECRET)
                .setContext(context);

        this.context=context;
    }
    public void get_data(){

        load=0;
        final RelativeLayout progress_layout=((Activity) context).findViewById(R.id.progress_layout);
        progress_layout.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Service.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        final Call<List<ServerData.get_service>> call = apiInterface.get_service(Library.getToken(context),page);

        Callback<List<ServerData.get_service>> callback=new Callback<List<ServerData.get_service>>() {
            @Override
            public void onResponse(Response<List<ServerData.get_service>> response, Retrofit retrofit) {

                if(response.isSuccess())
                {
                    load=1;
                    progress_layout.setVisibility(View.GONE);
                    show_data(response.body());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                load=1;
                progress_layout.setVisibility(View.GONE);
            }
        };
        call.enqueue(callback);
    }
    public void show_data(List<ServerData.get_service> data)
    {
        final LinearLayout box_data=((Activity) context).findViewById(R.id.box_data);

        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,10);
        for (ServerData.get_service get_service : data)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.service_row,null);
            view.setLayoutParams(layoutParams);

            ImageView static_map=(ImageView)view.findViewById(R.id.static_map);

            TextView date=(TextView)view.findViewById(R.id.date);
            //date.setText(Service.get_number(get_service.getDate()));

            setAddress(view,get_service);

            TextView status=(TextView)view.findViewById(R.id.status);
            if(get_service.getDriving_status()==2)
            {
                status.setText("اتمام سفر");
            }
            else {
                status.setText(Service.get_service_status(get_service.getStatus()));
            }

            TextView order_id=(TextView)view.findViewById(R.id.order_id);
            order_id.setText("شماره درخواست : "+get_service.getOrder_id());


            RelativeLayout other_data=(RelativeLayout)view.findViewById(R.id.other_data);
            other_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent j=new Intent(context,ServiceActivity.class);
                    j.putExtra("order_id",get_service.getOrder_id());
                    j.putExtra("address1",get_service.getAddress1());
                    j.putExtra("address2",get_service.getAddress2());
                    j.putExtra("address3",String.valueOf(get_service.getAddress3()));
                    if(get_service.getUserData()!=null)
                    {
                        j.putExtra("user_name",get_service.getUserData().getName());
                        j.putExtra("user_mobile",get_service.getUserData().getMobile());
                    }
                    j.putExtra("going_back",get_service.getGoing_back());
                    j.putExtra("price",get_service.getPrice());
                    j.putExtra("total_price",get_service.getTotal_price());
                    j.putExtra("stop_time",get_service.getStop_time());
                    j.putExtra("date",get_service.getDate());
                    context.startActivity(j);
                    ((Activity)context).overridePendingTransition(R.anim.enter,R.anim.exit);
                }
            });

            show_static_image(get_service,static_map);

            box_data.addView(view);
        }
    }
    public void show_static_image(ServerData.get_service data,ImageView static_map){

        double lat1=data.getLat1();
        double lat2=data.getLat2();
        double lng1=data.getLng1();
        double lng2=data.getLng2();
        double lat3=0;
        double lng3=0;
        double lat;
        double lng;
        if(String.valueOf(data.getLat3()).isEmpty()){
            lat=(lat1+lat2)/2;
            lng=(lng1+lng2)/2;
        }
        else{
            lat3=data.getLat3();
            lng3=data.getLng3();

            lat=(lat1+lat2+lat3)/3;
            lng=(lng1+lng2+lng3)/3;
        }


        LatLng centerPoint=new LatLng(lat,lng);

        int w=context.getResources().getDisplayMetrics().widthPixels-(Library.pxfromdp(context,30));
        Dimension dimension=new Dimension(w,250);

        ArrayList<CedarMaps.StaticMarker> markers=new ArrayList<>();


        markers.add(new CedarMaps.StaticMarker(new LatLng(lat1,lng1), Uri.parse("http://dl2.idehpardazanjavan.com/map_marker.png")));
        markers.add(new CedarMaps.StaticMarker(new LatLng(lat2,lng2),Uri.parse("http://dl2.idehpardazanjavan.com/destination_marker.png")));
        if(!String.valueOf(data.getLat3()).isEmpty()){
            markers.add(new CedarMaps.StaticMarker(new LatLng(lat3,lng3),Uri.parse("http://dl2.idehpardazanjavan.com/destination_marker.png")));
        }
        CedarMaps.getInstance().staticMap(dimension, 14, centerPoint, markers,
                new StaticMapImageResultListener() {
                    @Override
                    public void onSuccess(@NonNull Bitmap bitmap) {

                        static_map.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure(@NonNull String errorMessage) {

                    }
                });

    }
    public void change_page(){

        page=page+1;
    }
    public void setAddress(View view,ServerData.get_service get_service)
    {
        TextView address1=(TextView)view.findViewById(R.id.address1);
        TextView address2=(TextView)view.findViewById(R.id.address2);
        TextView address3=(TextView)view.findViewById(R.id.address3);

        address1.setText("مبدا : "+get_service.getAddress1());

        if(!String.valueOf(get_service.getAddress3()).isEmpty())
        {
            address3.setText("مقصد دوم : "+get_service.getAddress3());
            address2.setText("مقصد اول : "+get_service.getAddress2());
        }
        else
        {
            address2.setText("مقصد : "+get_service.getAddress2());
        }
    }
}
