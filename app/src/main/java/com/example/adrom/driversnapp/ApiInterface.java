package com.example.adrom.driversnapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by Hossein Saffarhamidi .
 */

public interface ApiInterface
{
    @FormUrlEncoded
    @POST("driver/login")
    Call<ServerData.login> login(@Field("mobile") String mobile,@Field("password") String password);


    @FormUrlEncoded
    @POST("driver/request_service")
    Call<ServerData.request_service> set_request(@Field("service_id") String request_service,@Field("token") String token,@Field("driver_lat") double driver_lat,@Field("driver_lng") double driver_lng);

    @FormUrlEncoded
    @POST("driver/set_status")
    Call<ServerData.status_service> set_status(@Field("service_id") String request_service,@Field("token") String token,@Field("status") int status
            ,@Field("driver_lat") double driver_lat,@Field("driver_lng") double driver_lng);

    @FormUrlEncoded
    @POST("driver/cancel_service")
    Call<String> cancel_service(@Field("service_id") String service_id,@Field("token") String token,@Field("status") int status);

    @FormUrlEncoded
    @POST("driver/set_request_status")
    Call<String> set_request_status(@Field("token") String token,@Field("status") int status);


    @FormUrlEncoded
    @POST("driver/set_driver_location")
    Call<String> set_driver_location(@Field("token") String token,@Field("lat") double lat,@Field("lng") double lng);

    @GET("driver/get_service")
    Call<List<ServerData.get_service>> get_service(@Header("x-access-token") String token, @Query("page") int page);
}