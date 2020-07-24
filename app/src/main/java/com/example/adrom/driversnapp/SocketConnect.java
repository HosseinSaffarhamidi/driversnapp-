package com.example.adrom.driversnapp;


import com.github.nkzawa.socketio.client.Socket;


public class SocketConnect
{
    static int today_price=0;
    static Socket socket;
    public static void set_Socket(Socket s)
    {
        socket=s;
    }
    public static Socket get_socket()
    {
        return  socket;
    }
}
