package ua.com.vando.apk_airplan;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private JSONArray Data;

    public SockClient(String aAddress, int aPort) {
        Address = aAddress;
        Port    = aPort;
        Clear();
    }

    public void Clear() {
        Data = new JSONArray();
    }

    public void Add(JSONObject aData) {
        Data.put(aData);
    }

    public void AddFunc(String aName, JSONArray aArgs) {
        JSONObject JO = new JSONObject();
        try {
            JO.put("Func", aName);
            JO.put("Args", aArgs);
            Data.put(JO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void AddFuncArr(String aName, int aPar1) {
        JSONArray JA = new JSONArray();
        JA.put(aPar1);
        AddFunc(aName, JA);
    }

    public void AddFuncArr(String aName, int aPar1, int aPar2) {
        JSONArray JA = new JSONArray();
        JA.put(aPar1);
        JA.put(aPar2);
        AddFunc(aName, JA);
    }

    public void SetPin(int aPin, int aValue) {
        AddFuncArr("SetPin",  aPin, aValue);
    }

    public void SetPwmOff(int aPin) {
        AddFuncArr("SetPwmOff", aPin);
    }

    public void SetPwmFreq(int aPin, int aValue) {
        AddFuncArr("SetPwmFreq", aPin, aValue);
    }

    public void SetPwmDuty(int aPin, int aValue) {
        AddFuncArr("SetPwmDuty", aPin, aValue);
    }

    public boolean Check() {
        boolean Result = false;
        //Result = new AsyncCheck().execute();
        return Result;
    }

    public void Send() {
        new AsyncSend().execute(Data.toString().getBytes());
        Clear();
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
