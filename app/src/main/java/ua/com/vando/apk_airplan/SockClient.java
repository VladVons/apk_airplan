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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// https://github.com/wouterverweirder/AIR-Mobile-UDP-Extension/blob/master/native/android/UDPSocketAndroidLibrary/src/be/aboutme/nativeExtensions/udp/UDPSocketAdapter.java

interface SockReceiveListener {
    public void doEvent(int aResult);
}

public class SockClient {
    private List Listeners = new ArrayList();
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

    public void Add(JSONObject aJO) {
        Data.put(aJO);
    }

    public void AddFunc(String aName, JSONArray aArgs) {
        JSONObject JO = new JSONObject();
        try {
            JO.put("Func", aName);
            JO.put("Args", aArgs);
            Add(JO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void AddFunc(String aName, int [] aArgs) {
        JSONArray JA = new JSONArray();
        for (int i = 0; i < aArgs.length; i++)
            JA.put(aArgs[i]);
        AddFunc(aName, JA);
    }

    public void AddFuncArr(String aName, int [] aPins, int [] aArgs) {
        JSONArray JA1 = new JSONArray();
        for (int i = 0; i < aPins.length; i++)
            JA1.put(aPins[i]);

        JSONArray JA2 = new JSONArray();
        JA2.put(JA1);
        for (int i = 0; i < aArgs.length; i++)
            JA2.put(aArgs[i]);
        AddFunc(aName, JA2);
    }


    public void SetPin(int aPin, int aValue) {
        AddFunc("SetPin",  new int [] { aPin, aValue});
    }

    public void SetPwmOff(int aPin) {
        AddFunc("SetPwmOff", new int [] {aPin});
    }

    public void SetPwmFreq(int aPin, int aValue) {
        AddFunc("SetPwmFreq", new int [] {aPin, aValue});
    }

    public void SetPwmDuty(int aPin, int aValue) {
        AddFunc("SetPwmDuty", new int [] {aPin, aValue});
    }

    public void SetPinArr(int [] aPins, int aValue) { AddFuncArr("SetPinArr", aPins, new int[] {aValue}); }

    public void SetPwmOffArr(int [] aPins) { AddFuncArr("SetPwmOffArr", aPins, new int[]{}); }

    public boolean Check() {
        boolean Result = false;
        //Result = new AsyncCheck().execute();
        return Result;
    }

    public void Send() {
        AddFunc("Print", new int [] {});
        new AsyncSend().execute(Data.toString().getBytes());
        Clear();
    }


    private void OnReceive(String aJson) throws JSONException {
        JSONArray JA = new JSONArray(aJson);
        for (int i = 0; i < JA.length(); i++) {
            JSONObject JO = JA.getJSONObject(i);
            String Func = JO.getString("Func");
            if (Func.equals("SetPwmDuty")) {
                int Result  = JO.getInt("Result");
                for (Object name : Listeners) {
                    ((SockReceiveListener)name).doEvent(Result);
                }
            }
        }
    }

    private class AsyncSend extends AsyncTask<byte[], String, DatagramPacket> {
        @Override
        protected DatagramPacket doInBackground(byte[]... aParams) {
            //Result = InetAddress.getByName(Address).isReachable(1000);
            //TimeUnit.SECONDS.sleep(1);

            DatagramSocket Socket;
            DatagramPacket PacketOut = null, PacketIn = null;

            byte[] aData = aParams[0];

            byte[] BufIn = new byte[1024];
            PacketIn = new DatagramPacket(BufIn, BufIn.length);

            try {
                InetAddress IAddress = InetAddress.getByName(Address);
                PacketOut = new DatagramPacket(aData, aData.length, IAddress, Port);

                Socket = new DatagramSocket();
                Socket.setSoTimeout(200);
                Socket.send(PacketOut);

                Socket.receive(PacketIn);
            } catch (SocketException e) {
                e.printStackTrace();
                //Log.i("Debug", e.getMessage());
            } catch (UnknownHostException e) {
                e.printStackTrace();
                //Log.i("Debug", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                //Log.i("Debug", e.getMessage());
            }

            return PacketIn;
        }

        @Override
        protected void onPostExecute(DatagramPacket result) {
            super.onPostExecute(result);

            try {
                String Result = new String(result.getData(), "UTF-8");
                OnReceive(Result);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
