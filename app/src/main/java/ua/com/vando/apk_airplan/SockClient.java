package ua.com.vando.apk_airplan;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;

//https://github.com/wouterverweirder/AIR-Mobile-UDP-Extension/blob/master/native/android/UDPSocketAndroidLibrary/src/be/aboutme/nativeExtensions/udp/UDPSocketAdapter.java

public class SockClient {
    private DatagramSocket Socket;
    private InetAddress Address;
    private int Port;

    //private DatagramChannel Channel;

    public SockClient(String aHost, int aPort) {
        Port = aPort;

        Address = null;
        try {
            Address = InetAddress.getByName(aHost);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            Socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            Log.i("Debug", e.getMessage());
        }
    }

    public void Send(byte[] aData) {
        DatagramPacket Packet = new DatagramPacket(aData, aData.length, Address, Port);
        new AsyncSockClient().execute(Packet);
    }

    public void OnReceive(String aString) {

    }

    private class AsyncSockClient extends AsyncTask<DatagramPacket, String, DatagramPacket> {
        @Override
        protected DatagramPacket doInBackground(DatagramPacket... aParams) {
            DatagramPacket Packet = aParams[0];
            try {
                Socket.send(Packet);
                Socket.receive(Packet);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Debug", e.getMessage());
            }
            return Packet;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            OnReceive(values[0]);
        }
    }
}
