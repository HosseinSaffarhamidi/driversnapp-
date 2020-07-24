package com.example.adrom.driversnapp;

import com.google.gson.annotations.SerializedName;

public class ServerData {

    public class login
    {
        @SerializedName("auth")
        String auth;

        @SerializedName("token")
        String token;

        @SerializedName("inventory")
        int inventory;

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getInventory() {
            return inventory;
        }

        public void setInventory(int inventory) {
            this.inventory = inventory;
        }
    }

    public class request_service
    {
        @SerializedName("status")
        String status;

        @SerializedName("user_name")
        String user_name;


        @SerializedName("mobile")
        String mobile;

        @SerializedName("user_inventory")
        int user_inventory;

        public int getUser_inventory() {
            return user_inventory;
        }

        public void setUser_inventory(int user_inventory) {
            this.user_inventory = user_inventory;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }


        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

    public class status_service
    {
        @SerializedName("status")
        String status;

        @SerializedName("inventory")
        int inventory;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getInventory() {
            return inventory;
        }

        public void setInventory(int inventory) {
            this.inventory = inventory;
        }
    }


    public class get_service
    {
        @SerializedName("address1")
        String address1;

        @SerializedName("address2")
        String address2;

        @SerializedName("address3")
        String address3;

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getAddress3() {
            return address3;
        }

        public void setAddress3(String address3) {
            this.address3 = address3;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @SerializedName("date")
        String date;

        @SerializedName("driving_status")
        int driving_status;

        public int getDriving_status() {
            return driving_status;
        }

        public void setDriving_status(int driving_status) {
            this.driving_status = driving_status;
        }

        @SerializedName("status")
        int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @SerializedName("order_id")
        String order_id;

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }


        @SerializedName("lat1")
        double lat1;

        @SerializedName("lat2")
        double lat2;

        @SerializedName("lat3")
        double lat3;


        @SerializedName("lng1")
        double lng1;

        @SerializedName("lng2")
        double lng2;

        @SerializedName("lng3")
        double lng3;

        public double getLng1() {
            return lng1;
        }

        public void setLng1(double lng1) {
            this.lng1 = lng1;
        }

        public double getLng2() {
            return lng2;
        }

        public void setLng2(double lng2) {
            this.lng2 = lng2;
        }

        public double getLng3() {
            return lng3;
        }

        public void setLng3(double lng3) {
            this.lng3 = lng3;
        }

        public double getLat1() {
            return lat1;
        }

        public void setLat1(double lat1) {
            this.lat1 = lat1;
        }

        public double getLat2() {
            return lat2;
        }

        public void setLat2(double lat2) {
            this.lat2 = lat2;
        }

        public double getLat3() {
            return lat3;
        }

        public void setLat3(double lat3) {
            this.lat3 = lat3;
        }

        @SerializedName("_id")
        String _id;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        @SerializedName("User")
        UserData userData;

        @SerializedName("going_back")
        String going_back;

        @SerializedName("price")
        String price;

        @SerializedName("total_price")
        String total_price;

        public UserData getUserData() {
            return userData;
        }

        public void setUserData(UserData userData) {
            this.userData = userData;
        }

        @SerializedName("stop_time")
        String stop_time;

        public String getGoing_back() {
            return going_back;
        }

        public void setGoing_back(String going_back) {
            this.going_back = going_back;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getTotal_price() {
            return total_price;
        }

        public void setTotal_price(String total_price) {
            this.total_price = total_price;
        }

        public String getStop_time() {
            return stop_time;
        }

        public void setStop_time(String stop_time) {
            this.stop_time = stop_time;
        }


    }

    public class UserData
    {
        @SerializedName("name")
        String name;

        @SerializedName("mobile")
        String mobile;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }



}
