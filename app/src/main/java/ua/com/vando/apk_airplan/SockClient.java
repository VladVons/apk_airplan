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
    //private AsyncTimer ATimer;

    public SockClient(String aAddress, int aPort) {
        Address = aAddress;
        Port    = aPort;

        //ATimer = new AsyncTimer();
        //ATimer.execute();
        //ATimer.cancel(false);
    }

    public void Send(Serial aSerial) {
        aSerial.AddFunc("Print", new int [] {});
        String Data = aSerial.GetData();
        new AsyncSend().execute(Data.getBytes());
    }

    public boolean Check() {
        boolean Result = false;
        //Result = new AsyncCheck().execute();
        return Result;
    }

    private void OnReceive(DatagramPacket aPacket) throws JSONException {
        //JSONParser parser;

        String JsonStr = null;
        try {
            JsonStr = new String(aPacket.getData(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JSONObject JO = new JSONObject(JsonStr);
        JSONArray Data = JO.getJSONArray("Data");
        for (int i = 0; i < Data.length(); i++) {
            JSONObject Item = Data.getJSONObject(i);
            String ID = JO.getString("ID");
            //if (ID.equals("SetPwmDuty")) {

        }


        /*
        JSONArray JA = new JSONArray(Data);
        for (int i = 0; i < JA.length(); i++) {
            JSONObject JO = JA.getJSONObject(i);
            String ID = JO.getString("ID");
            if (ID.equals("SetPwmDuty")) {
                int Result  = JO.getInt("Result");
                for (Object name : Listeners) {
                    ((SockReceiveListener)name).doEvent(Result);
                }
            }
        }
        */
    }

    private class AsyncSend extends AsyncTask<byte[], String, DatagramPacket> {
        @Override
        protected DatagramPacket doInBackground(byte[]... aParams) {
            //Result = InetAddress.getByName(Address).isReachable(1000);
            //TimeUnit.SECONDS.sleep(1);

            DatagramSocket Socket;
            DatagramPacket PacketOut = null, PacketIn = null;

            byte[] aData = aParams[0];

            byte[] BufIn = new byte[2048];
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
                OnReceive(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

/*
    private class AsyncTimer extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (isCancelled() == false) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    publishProgress(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
*/
