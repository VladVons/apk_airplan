package ua.com.vando.apk_airplan;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private static int cPreferencesId = 1;

    private boolean prefGravityBind;
    private String  prefServerAddr;
    private int     prefServerPort, prefMotorMin, prefMotorMax;

    private TextView txtInfo;
    private EditText edtSend;
    private SeekBar  sbMotor1, sbMotor2;

    SockClient sockClient;
    Gravity gravity;
    FrmMotorDC frmMotorDC1, frmMotorDC2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtInfo    = (TextView) findViewById(R.id.txtInfo);
        edtSend    = (EditText) findViewById(R.id.edtSend);

        sbMotor1   = (SeekBar)  findViewById(R.id.sbMotor1);
        sbMotor2   = (SeekBar)  findViewById(R.id.sbMotor2);

        LoadPreferences();

        gravity = new Gravity(this);
        gravity.registerListener(GravityMotorDC);

        sockClient = new SockClient(prefServerAddr, prefServerPort);
        sockClient.Check();


        frmMotorDC1 = new FrmMotorDC(this, R.id.txtMotor1, R.id.sbMotor1);
        frmMotorDC1.Init(cMotorDC1A, cMotorDC1B, sockClient);
        frmMotorDC1.SetRange(prefMotorMin, prefMotorMax);

        frmMotorDC2 = new FrmMotorDC(this, R.id.txtMotor2, R.id.sbMotor2);
        frmMotorDC2.Init(cMotorDC2A, cMotorDC2B, sockClient);
        frmMotorDC2.SetRange(prefMotorMin, prefMotorMax);

        FrmLamp frmLamp;
        frmLamp = new FrmLamp(this, R.id.txtLampRed,   R.id.cbLampRed);
        frmLamp.Init(cLampRed, sockClient);;

        frmLamp = new FrmLamp(this, R.id.txtLampGreen, R.id.cbLampGreen);
        frmLamp.Init(cLampGreen, sockClient);;

        frmLamp = new FrmLamp(this, R.id.txtLampBlue,  R.id.cbLampBlue);
        frmLamp.Init(cLampBlue, sockClient);;

        frmLamp = new FrmLamp(this, R.id.txtLampSys,  R.id.cbLampSys);
        frmLamp.Init(cLampSys, sockClient);;

        String IP = Util.GetOwnIP(this);
        IP = (IP == "" ? "No connection" : "Current IP " + IP);
        txtInfo.setText(IP);
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
        } else if (id == R.id.action_exit) {
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnSendOnClick(View view) throws JSONException {
        String str = edtSend.getText().toString();
        JSONObject JO = new JSONObject(str);
        sockClient.Clear();
        sockClient.Add(JO);
        sockClient.Send();
    }

    public void cbMotorRevOnClick(View view) {
        boolean Checked = ((CheckBox) view).isChecked();
        frmMotorDC1.SetReverse(Checked);
        frmMotorDC2.SetReverse(Checked);
    }

    public void btnMotorStopOnClick(View view) {
        frmMotorDC1.Stop();
        frmMotorDC2.Stop();
    }

    public void btnMotorStartOnClick(View view) {
        frmMotorDC1.Start();
        frmMotorDC2.Start();
    }

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

        sockClient.Clear();
        sockClient.SetPinArr(new int[] {cLampRed, cLampGreen, cLampBlue, cLampSys}, Checked ? 1 : 0);
        sockClient.SetPwmOffArr(new int[] {cLampRed, cLampGreen, cLampBlue, cLampSys});
        sockClient.Send();
    }

    private GravityListener GravityMotorDC  = new GravityListener() {
        public int MaxGravity = 10;

        @Override
        public void doEvent(int aX, int aY) {
            int MaxValue = prefMotorMax;

            int HalfGravity = MaxGravity / 2;
            int AY = Util.InRange(aY, -HalfGravity, HalfGravity);
            int AX = Util.InRange(aX, -HalfGravity, HalfGravity);

            int ValueY = (MaxValue / 2) + (MaxValue / MaxGravity * AY);
            int ValueX = (MaxValue / MaxGravity * AX);

            if (prefGravityBind) {
                sbMotor1.setProgress(Util.InRange(ValueY + ValueX, 0, MaxValue));
                sbMotor2.setProgress(Util.InRange(ValueY - ValueX, 0, MaxValue));

                String Str = String.format(Locale.getDefault(), "X=%d, Y=%d", AX, AY);
                txtInfo.setText(Str);
            }
        }
    };

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // clear all Preferences
        //sharedPreferences.edit().clear().commit();

        // ???
        //prefPort = Integer.parseInt(sharedPreferences.getInt("preference_server_port", 51016);
        String Value = sharedPreferences.getString("preference_server_port", "51015");
        prefServerPort   = Integer.parseInt(Value);

        prefServerAddr   = sharedPreferences.getString("preference_server_address", "192.168.4.1");
        prefGravityBind  = sharedPreferences.getBoolean("preference_gravitybind", false);

        Value = sharedPreferences.getString("preference_motor_min", "0");
        prefMotorMin   = Integer.parseInt(Value);

        Value = sharedPreferences.getString("preference_motor_max", "999");
        prefMotorMax   = Integer.parseInt(Value);
    }

    @Override
    // called from startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cPreferencesId) {
            LoadPreferences();

            frmMotorDC1.SetRange(prefMotorMin, prefMotorMax);
            frmMotorDC2.SetRange(prefMotorMin, prefMotorMax);
        }
    }

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
    }
}
