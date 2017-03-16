package ua.com.vando.apk_airplan;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;


public class ClientActivity extends Activity {
    private static int cLampRed = 15, cLampGreen = 12, cLampBlue = 13;
    private static int cMotorDC1 = 12, cMotorDC2 = 14;

    private TextView txtInfo, txtGravity;
    private EditText edtServer, edtPort, edtSend;
    private SeekBar  sbMotor1, sbMotor2;
    private CheckBox cbBind;

    SockClient sockClient;
    Gravity gravity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        txtInfo    = (TextView) findViewById(R.id.txtInfo);
        txtGravity = (TextView) findViewById(R.id.txtGravity);
        edtSend    = (EditText) findViewById(R.id.edtSend);
        edtServer  = (EditText) findViewById(R.id.edtServer);
        edtPort    = (EditText) findViewById(R.id.edtPort);

        sbMotor1   = (SeekBar)  findViewById(R.id.sbMotor1);
        sbMotor2   = (SeekBar)  findViewById(R.id.sbMotor2);
        cbBind     = (CheckBox) findViewById(R.id.cbBind);

        Config config = new Config(this);
        config.LoadView(edtServer, "Server", "192.168.4.1");
        config.LoadView(edtPort,   "Port", "51015");

        gravity = new Gravity(this);
        gravity.registerListener(GravityMotorDC);

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


    private GravityListener GravityMotorDC  = new GravityListener() {
        public int MaxGravity = 10, MaxValue = 1023;

        private int InRange(int aValue, int aMin, int aMax) {
            if (aValue < aMin)
                aValue = aMin;
            if (aValue > aMax)
                aValue = aMax;
            return aValue;
        }

        @Override
        public void doEvent(int aX, int aY) {
            int HalfGravity = MaxGravity / 2;
            int AY = InRange(aY, -HalfGravity, HalfGravity);
            int AX = InRange(aX, -HalfGravity, HalfGravity);

            int ValueY = (MaxValue / 2) + (MaxValue / MaxGravity * AY);
            int ValueX = (MaxValue / MaxGravity * AX);

            if (cbBind.isChecked()) {
                sbMotor1.setProgress(InRange(ValueY + ValueX, 0, MaxValue));
                sbMotor2.setProgress(InRange(ValueY - ValueX, 0, MaxValue));
            }

            String Str = String.format(Locale.getDefault(), "(X=%d) (Y=%d)", AX, AY);
            txtGravity.setText(Str);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        gravity.Start();
    }

    @Override
    public void onStop() {
        super.onStop();
        gravity.Stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Config config = new Config(this);
        config.SaveView(edtServer, "Server");
        config.SaveView(edtPort, "Port");
    }
}
