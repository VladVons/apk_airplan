package ua.com.vando.apk_airplan;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;

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
    TextView txtvReport;
    EditText edtServer, edtPort;
    private Socket socket = null;
    private static final String SERVERPORT = "51018";
    private static final String SERVER_IP = "192.168.2.12";

    SensorManager mSensorManager;
    Sensor mSensor;
    int prevAX, prevAY, prevAZ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        txtvReport = (TextView) findViewById(R.id.txtvReport);
        edtServer  = (EditText) findViewById(R.id.edtServer);
        edtPort    = (EditText) findViewById(R.id.edtPort);

        edtServer.setText(SERVER_IP);
        edtPort.setText(SERVERPORT);

        //https://github.com/akexorcist/Android-Sensor-Gyroscope/blob/master/src/app/akexorcist/sensor_gyroscope/Main.java
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(gyroListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void btnConnectOnClick(View view) {
        new Thread(new ClientThread()).start();
    }

    public void btnSendOnClick(View view) {
        try {
            EditText et = (EditText) findViewById(R.id.edtText);
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

    class ClientThread implements Runnable {
        @Override
        public void run() {
            String Adr = edtServer.getText().toString();
            int Port = Integer.parseInt(edtPort.getText().toString());
            try {
                socket = new Socket(Adr, Port);
            } catch (Exception e) {
                e.printStackTrace();
                String Str = String.format(Locale.getDefault(), "%s.%s.%d", e.getMessage(), Adr, Port);
                txtvReport.setText(Str);
            }
        }
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int acc) {
            txtvReport.setText("onAccuracyChanged: ");
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            //if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            int AX = Math.round(event.values[0] * 1);
            int AY = Math.round(event.values[1] * 1);
            int AZ = Math.round(event.values[2] * 1);

            if (prevAX + prevAY + prevAZ != AX + AY + AZ) {
                String Str = String.format(Locale.getDefault(), "(X %d) (Y %d) (Z %d)", AX, AY, AZ);
                txtvReport.setText("onSensorChanged: " + Str);

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


}
