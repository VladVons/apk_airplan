package ua.com.vando.apk_airplan;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

//http://androiddocs.ru/parsing-json-poluchaem-i-razbiraem-json-s-vneshnego-resursa/
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//JSON
//http://androiddocs.ru/parsing-json-poluchaem-i-razbiraem-json-s-vneshnego-resursa/

/* Client
https://www.androidpit.com/forum/712627/android-socket-client
https://www.researchgate.net/publication/303737681_SOCKET_PROGRAMMING_WIFI_CHAT_APP_FOR_ANDROID_SMARTPHONE
http://www.pvsm.ru/java/10098
https://habrahabr.ru/sandbox/31311/
https://guides.codepath.com/android/Sending-and-Receiving-Data-with-Sockets
http://stackoverflow.com/questions/5893911/android-client-socket-how-to-read-data
*/

public class ClientActivity extends Activity {
    private TextView txtInfo;
    private EditText edtServer, edtPort, edtSend;
    private Socket socket = null;
    SockClient sockClient;

    SensorManager mSensorManager;
    Sensor mSensor;
    int prevAX, prevAY, prevAZ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        txtInfo    = (TextView) findViewById(R.id.txtInfo);
        edtSend    = (EditText) findViewById(R.id.edtSend);
        edtServer  = (EditText) findViewById(R.id.edtServer);
        edtPort    = (EditText) findViewById(R.id.edtPort);

        ////https://github.com/akexorcist/Android-Sensor-Gyroscope/blob/master/src/app/akexorcist/sensor_gyroscope/Main.java
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ////mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener(gyroListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        Config config = new Config(this);
        config.LoadView(edtServer, "Server", "192.168.1.1");
        config.LoadView(edtPort,   "Port", "51015");

        new FrmMotorDC(this, R.id.txtMotor1, R.id.sbMotor1);
        new FrmMotorDC(this, R.id.txtMotor2, R.id.sbMotor2);

        new FrmLamp(this, R.id.txtLampRed,   R.id.cbLampRed);
        new FrmLamp(this, R.id.txtLampGreen, R.id.cbLampGreen);
        new FrmLamp(this, R.id.txtLampBlue,  R.id.cbLampBlue);

        String IP = Net.GetOwnIP(this);
        IP = (IP == "" ? "No connection" : "Current IP " + IP);
        txtInfo.setText(IP);

        sockClient = new SockClient();
        sockClient.txtInfo = txtInfo;
    }

    public void btnSendOnClick(View view) {
        try {
            EditText et = (EditText) findViewById(R.id.edtSend);
            String str = et.getText().toString();

            JSONObject json = new JSONObject();
            json.put("Nombre", str);

            String strj = String.format(Locale.getDefault(), "{\"Type\": \"Func\", \"Name\": \"TAppMan.GetEcho\", \"Arg\": [\"%s\"]}", str);

            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            out.println(strj);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnConnectOnSend(View view) throws IOException {
        sockClient.execute(edtSend.getText().toString());
    }

    public void btnConnectOnClick(View view) throws IOException {
        //Thread thread = new Thread(new ClientThread());
        //thread.start()
        byte[] send_data = new byte[1024];
        String str = "{test}";
        send_data = str.getBytes();


        InetAddress IPAddress =  InetAddress.getByName("127.0.0.1");
        DatagramSocket client_socket = new DatagramSocket(51015, IPAddress);

        DatagramPacket send_packet = new DatagramPacket(send_data, str.length(), IPAddress, 2362);
        client_socket.send(send_packet);
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            String Adr = edtServer.getText().toString();
            Adr = "192.168.10.11";
            int Port = Integer.parseInt(edtPort.getText().toString());

            //TextView txtInfo1    = (TextView) findViewById(R.id.txtInfo);
            //txtInfo.setText("ClientThread");
            prevAX = 1;

            String ConnStat = "Connected";
            try {
                Log.i("Debug", "inside try1");
                socket = new Socket(Adr, Port);
                Log.i("Debug", "inside try2");
            } catch (Exception e) {
                e.printStackTrace();
                ConnStat = String.format(Locale.getDefault(), "%s. %s. %d", e.getMessage(), Adr, Port);
                Log.i("Debug", ConnStat);
            }
            Log.i("Debug", "outside try1");
            //txtInfo.setText(ConnStat);

        }
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int acc) {
            txtInfo.setText("onAccuracyChanged: ");
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            //if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            int AX = Math.round(event.values[0] * 1);
            int AY = Math.round(event.values[1] * 1);
            int AZ = Math.round(event.values[2] * 1);

            if (prevAX + prevAY + prevAZ != AX + AY + AZ) {
                String Str = String.format(Locale.getDefault(), "(X %d) (Y %d) (Z %d)", AX, AY, AZ);
                txtInfo.setText("onSensorChanged: " + Str);

                prevAX = AX;
                prevAY = AY;
                prevAZ = AZ;

                if (socket != null) {
                    try {
                        String strj = String.format(Locale.getDefault(), "{\"Type\": \"Func\", \"Name\": \"TAppMan.GetEcho\", \"Arg\": [\"%s\"]}", Str);

                        PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                                true);
                        out.println(strj);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(gyroListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(gyroListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Config config = new Config(this);
        config.SaveView(edtServer, "Server");
        config.SaveView(edtPort, "Port");
    }

}
