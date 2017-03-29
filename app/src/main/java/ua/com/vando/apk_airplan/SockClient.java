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

    public void AddFuncVal(String aName, int aPin) {
        JSONArray JA = new JSONArray();
        JA.put(aPin);
        AddFunc(aName, JA);
    }

    public void AddFuncVal(String aName, int aPin, int aValue) {
        JSONArray JA = new JSONArray();
        JA.put(aPin);
        JA.put(aValue);
        AddFunc(aName, JA);
    }

    public void AddFuncVal(String aName, int [] aPins, int aValue) {
        JSONArray JA1 = new JSONArray();
        for (int i = 0; i < aPins.length; i++)
            JA1.put(aPins[i]);

        JSONArray JA2 = new JSONArray();
        JA2.put(JA1);
        JA2.put(aValue);
        AddFunc(aName, JA2);
    }

    public void AddFuncVal(String aName, int [] aPins) {
        JSONArray JA1 = new JSONArray();
        for (int i = 0; i < aPins.length; i++)
            JA1.put(aPins[i]);

        JSONArray JA2 = new JSONArray();
        JA2.put(JA1);
        AddFunc(aName, JA2);
    }


    public void SetPin(int aPin, int aValue) {
        AddFuncVal("SetPin",  aPin, aValue);
    }

    public void SetPwmOff(int aPin) {
        AddFuncVal("SetPwmOff", aPin);
    }

    public void SetPwmFreq(int aPin, int aValue) {
        AddFuncVal("SetPwmFreq", aPin, aValue);
    }

    public void SetPwmDuty(int aPin, int aValue) {
        AddFuncVal("SetPwmDuty", aPin, aValue);
    }

    public void SetPinArr(int [] aPins, int aValue) { AddFuncVal("SetPinArr", aPins, aValue); }

    public void SetPwmOffArr(int [] aPins) { AddFuncVal("SetPwmOffArr", aPins); }

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
