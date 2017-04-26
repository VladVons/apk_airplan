package ua.com.vando.apk_airplan;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
//
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class ActivityMain extends AppCompatActivity {
    private static int cLampRed = 15, cLampGreen = 12, cLampBlue = 13, cLampSys = 02;
    private static int cMotorDC1A = 12, cMotorDC1B = 13, cMotorDC2A = 14, cMotorDC2B = 15;
    private static int cMotorServ1 = 5;
    private static int cPreferencesId = 1;

    private boolean prefGravityBind, prefMotorStopOnExit, MotorStarted = false;
    private String prefServerAddr;
    private int prefServerPort, prefMotorMin, prefMotorMax;

    private TextView txtInfo;
    private EditText edtSend;
    private SeekBar  sbMotorDC1, sbMotorDC2, sbMotorServ1;
    private Button   btnMotorStart;
    private CheckBox cbMotorRev;

    SockClient sockClient;
    Gravity gravity;
    FrmMotorDC frmMotorDC1, frmMotorDC2;
    FrmMotorServ frmMotorServ1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtInfo = (TextView) findViewById(R.id.txtInfo);
        edtSend = (EditText) findViewById(R.id.edtSend);

        sbMotorDC1   = (SeekBar) findViewById(R.id.sbMotorDC1);
        sbMotorDC2   = (SeekBar) findViewById(R.id.sbMotorDC2);
        sbMotorServ1 = (SeekBar) findViewById(R.id.sbMotorServ1);
        cbMotorRev = (CheckBox)  findViewById(R.id.cbMotorRev);
        btnMotorStart = (Button)  findViewById(R.id.btnMotorStart);


        LoadPreferences();

        gravity = new Gravity(this);
        gravity.registerListener(GravityMotorDC);

        sockClient = new SockClient(prefServerAddr, prefServerPort);
        //sockClient.Check();


        frmMotorDC1 = new FrmMotorDC(this, R.id.txtMotorDC1, R.id.sbMotorDC1);
        frmMotorDC1.Init(cMotorDC1A, cMotorDC1B, sockClient);
        frmMotorDC1.SetScrollRange(prefMotorMin, prefMotorMax);

        frmMotorDC2 = new FrmMotorDC(this, R.id.txtMotorDC2, R.id.sbMotorDC2);
        frmMotorDC2.Init(cMotorDC2A, cMotorDC2B, sockClient);
        frmMotorDC2.SetScrollRange(prefMotorMin, prefMotorMax);

        frmMotorServ1 = new FrmMotorServ(this, R.id.txtMotorServ1, R.id.sbMotorServ1);
        frmMotorServ1.Init(cMotorServ1, sockClient);
        frmMotorServ1.SetScrollRange(0, 200);
        frmMotorServ1.SetHardRange(40, 100);

        FrmLamp frmLamp;
        frmLamp = new FrmLamp(this, R.id.txtLampRed, R.id.cbLampRed);
        frmLamp.Init(cLampRed, sockClient);

        frmLamp = new FrmLamp(this, R.id.txtLampGreen, R.id.cbLampGreen);
        frmLamp.Init(cLampGreen, sockClient);

        frmLamp = new FrmLamp(this, R.id.txtLampBlue, R.id.cbLampBlue);
        frmLamp.Init(cLampBlue, sockClient);

        frmLamp = new FrmLamp(this, R.id.txtLampSys, R.id.cbLampSys);
        frmLamp.Init(cLampSys, sockClient);

        String IP = Util.GetOwnIP(this);
        IP = (IP == "" ? "No connection" : "Current IP " + IP);
        txtInfo.setText(IP);

        MotorStart(MotorStarted);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent Intent;

        int id = item.getItemId();
        if (id == R.id.action_preference) {
            Intent = new Intent(ActivityMain.this, ActivityPreferences.class);
            startActivityForResult(Intent, cPreferencesId);
        } else if (id == R.id.action_about) {
            Intent = new Intent(ActivityMain.this, ActivityAbout.class);
            startActivity(Intent);
        } else if (id == R.id.action_dialog_motor_dc) {
            Intent = new Intent(ActivityMain.this, ActivityDialogMotorDC.class);
            startActivity(Intent);
        } else if (id == R.id.action_exit) {
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnSendOnClick(View view) throws JSONException {
        String str = edtSend.getText().toString();
        JSONObject JO = new JSONObject(str);

        Serialize serial = new Serialize();
        serial.AddData(JO);
        sockClient.Send(serial);
    }

    public void cbMotorRevOnClick(View view) {
        boolean Checked = ((CheckBox) view).isChecked();
        frmMotorDC1.SetReverse(Checked);
        frmMotorDC2.SetReverse(Checked);
    }

    public void btnMotorStartOnClick(View view) {
        MotorStarted = !MotorStarted;
        MotorStart(MotorStarted);

        Serialize serial = new Serialize();
        serial.SetLogLevel(0);
        sockClient.Send(serial);
    }

/*
    public void cbLampAllOnClick(View view) {
        boolean Checked = ((CheckBox) view).isChecked();

        CheckBox cb = (CheckBox) findViewById(R.id.cbLampRed);
        cb.setChecked(Checked);
        cb = (CheckBox) findViewById(R.id.cbLampGreen);
        cb.setChecked(Checked);
        cb = (CheckBox) findViewById(R.id.cbLampBlue);
        cb.setChecked(Checked);
        cb = (CheckBox) findViewById(R.id.cbLampSys);
        cb.setChecked(Checked);

        Serial serial = new Serial();
        serial.SetPwmOffArr(new int[]{cLampRed, cLampGreen, cLampBlue, cLampSys});
        serial.SetPinArr(new int[]{cLampRed, cLampGreen, cLampBlue, cLampSys}, Checked ? 1 : 0);
        sockClient.Send(serial);
    }
*/

    private GravityListener GravityMotorDC = new GravityListener() {
        public int MaxGravity = 100;

        @Override
        public void doEvent(int aX, int aY) {
            int MaxValue = prefMotorMax;

            int HalfGravity = MaxGravity / 2;
            int AY = Util.InRange(aY, -HalfGravity, HalfGravity);
            int AX = Util.InRange(aX, -HalfGravity, HalfGravity);

            int ValueY = (MaxValue / 2) + (MaxValue / MaxGravity * AY);
            int ValueX = (MaxValue / MaxGravity * AX);
            int Serv1  = (frmMotorServ1.GetScrollMax() / 2) + (frmMotorServ1.GetScrollMax() / MaxGravity * AX);

            sbMotorServ1.setProgress(Serv1);

            if (prefGravityBind && MotorStarted) {
                int DC1 = Util.InRange(ValueY + ValueX, 0, MaxValue);
                int DC2 = Util.InRange(ValueY - ValueX, 0, MaxValue);
                sbMotorDC1.setProgress(DC1);
                sbMotorDC2.setProgress(DC2);

                //String Str = String.format(Locale.getDefault(), "X=%d, Y=%d, DC1=%d, DC2=%d, Serv1=%d", AX, AY, DC1, DC2, Serv1);
                String Str = String.format(Locale.getDefault(), "X=%d, Y=%d", AX, AY);
                txtInfo.setText(Str);
            }
        }
    };

    private void LoadPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // clear all Preferences
        //sharedPreferences.edit().clear().commit();

        // ???
        //prefPort = Integer.parseInt(sharedPreferences.getInt("preference_server_port", 51016);
        String Value = sharedPreferences.getString("pref_server_port", "51015");
        prefServerPort = Integer.parseInt(Value);

        prefServerAddr = sharedPreferences.getString("pref_server_address", "192.168.4.1");
        prefGravityBind = sharedPreferences.getBoolean("pref_gravitybind", false);
        prefMotorStopOnExit = sharedPreferences.getBoolean("pref_motor_stoponexit", true);

        Value = sharedPreferences.getString("pref_motor_min", "0");
        prefMotorMin = Integer.parseInt(Value);

        Value = sharedPreferences.getString("pref_motor_max", "999");
        prefMotorMax = Integer.parseInt(Value);
    }

    @Override
    // called from startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cPreferencesId) {
            LoadPreferences();

            frmMotorDC1.SetScrollRange(prefMotorMin, prefMotorMax);
            frmMotorDC2.SetScrollRange(prefMotorMin, prefMotorMax);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MotorStart(MotorStarted);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (prefMotorStopOnExit) {
            MotorStarted = false;
            MotorStart(MotorStarted);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void MotorStart(boolean aStart) {
        if (aStart) {
            btnMotorStart.setText("Stop");
            gravity.Start();
        } else {
            btnMotorStart.setText("Start");
            gravity.Stop();
        }

        cbMotorRev.setEnabled(aStart);
        sbMotorDC1.setEnabled(aStart);
        sbMotorDC2.setEnabled(aStart);
        sbMotorServ1.setEnabled(aStart);
        frmMotorDC1.Start(aStart);
        frmMotorDC2.Start(aStart);
    }
}


