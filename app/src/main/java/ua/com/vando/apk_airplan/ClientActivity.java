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
    private static int cLampRed = 15, cLampGreen = 12, cLampBlue = 13;
    private static int cMotorDC1 = 12, cMotorDC2 = 14;

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
        config.LoadView(edtServer, "Server", "192.168.4.1");
        config.LoadView(edtPort,   "Port", "51015");

        sockClient = new SockClient(edtServer.getText().toString(), Integer.valueOf(edtPort.getText().toString()));

        FrmMotorDC frmMotorDC;
        frmMotorDC = new FrmMotorDC(this, R.id.txtMotor1, R.id.sbMotor1);
        frmMotorDC.Init(cMotorDC1, sockClient);

        frmMotorDC = new FrmMotorDC(this, R.id.txtMotor2, R.id.sbMotor2);
        frmMotorDC.Init(cMotorDC2, sockClient);

        FrmLamp frmLamp;
        frmLamp = new FrmLamp(this, R.id.txtLampRed,   R.id.cbLampRed);
        frmLamp.Init(cLampRed, sockClient);;

        frmLamp = new FrmLamp(this, R.id.txtLampGreen, R.id.cbLampGreen);
        frmLamp.Init(cLampGreen, sockClient);;

        frmLamp = new FrmLamp(this, R.id.txtLampBlue,  R.id.cbLampBlue);
        frmLamp.Init(cLampBlue, sockClient);;

        String IP = Net.GetOwnIP(this);
        IP = (IP == "" ? "No connection" : "Current IP " + IP);
        txtInfo.setText(IP);

   }

    public void btnSendOnClick(View view) {
        String str = edtSend.getText().toString();
        sockClient.Send(str.getBytes());
    }

    public void btnConnOnClick(View view) throws IOException {
        sockClient = new SockClient(edtServer.getText().toString(), Integer.valueOf(edtPort.getText().toString()));
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
