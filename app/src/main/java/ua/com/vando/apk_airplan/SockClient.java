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
import java.util.concurrent.TimeUnit;

// https://github.com/wouterverweirder/AIR-Mobile-UDP-Extension/blob/master/native/android/UDPSocketAndroidLibrary/src/be/aboutme/nativeExtensions/udp/UDPSocketAdapter.java

public class SockClient {
    private String Address;
    private int Port;

    public SockClient(String aAddress, int aPort) {
        Address = aAddress;
        Port    = aPort;
    }

    public boolean Check() {
        boolean Result = false;
        Result = new AsyncCheck().execute();
        return Result;
    }

    public void Send(byte[] aData) {
        new AsyncSend().execute(aData);
    }

    public void OnReceive(String aString) {

    }

    private class AsyncCheck extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean Result = false;
            try {
                Result = InetAddress.getByName(Address).isReachable(1000);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Debug", e.getMessage());
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }
    }

    private class AsyncSend extends AsyncTask<byte[], String, DatagramPacket> {
        @Override
        protected DatagramPacket doInBackground(byte[]... aParams) {
            byte[] aData = aParams[0];
            DatagramSocket Socket;
            DatagramPacket Packet = null;

            try {
                InetAddress IAddress = InetAddress.getByName(Address);
                Packet = new DatagramPacket(aData, aData.length, IAddress, Port);

                Socket = new DatagramSocket();
                Socket.setSoTimeout(200);
                Socket.send(Packet);
                //Socket.receive(Packet);
            } catch (SocketException e) {
                e.printStackTrace();
                Log.i("Debug", e.getMessage());
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.i("Debug", e.getMessage());
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
