package ua.com.vando.apk_airplan;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
//
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static int cLampRed = 15, cLampGreen = 12, cLampBlue = 13;
    private static int cMotorDC1A = 12, cMotorDC1B = 13, cMotorDC2 = 14;
    private static int cPreferencesCode = 1;

    private boolean prefGravityBind;
    private String  prefServer;
    private int     prefPort;

    private TextView txtInfo;
    private EditText edtSend;
    private SeekBar  sbMotor1, sbMotor2;

    SockClient sockClient;
    Gravity gravity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        txtInfo    = (TextView) findViewById(R.id.txtInfo);
        edtSend    = (EditText) findViewById(R.id.edtSend);

        sbMotor1   = (SeekBar)  findViewById(R.id.sbMotor1);
        sbMotor2   = (SeekBar)  findViewById(R.id.sbMotor2);

        LoadPreferences();

        gravity = new Gravity(this);
        gravity.registerListener(GravityMotorDC);

        sockClient = new SockClient(prefServer, prefPort);
        sockClient.Check();


        FrmMotorDC frmMotorDC;
        frmMotorDC = new FrmMotorDC(this, R.id.txtMotor1, R.id.sbMotor1);
        frmMotorDC.Init(cMotorDC1A, sockClient);

        //frmMotorDC = new FrmMotorDC(this, R.id.txtMotor2, R.id.sbMotor2);
        //frmMotorDC.Init(cMotorDC2, sockClient);

        FrmLamp frmLamp;
        frmLamp = new FrmLamp(this, R.id.txtLampRed,   R.id.cbLampRed);
        frmLamp.Init(cLampRed, sockClient);;

        frmLamp = new FrmLamp(this, R.id.txtLampGreen, R.id.cbLampGreen);
        frmLamp.Init(cLampGreen, sockClient);;

        frmLamp = new FrmLamp(this, R.id.txtLampBlue,  R.id.cbLampBlue);
        frmLamp.Init(cLampBlue, sockClient);;

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
        int id = item.getItemId();
        if (id == R.id.action_preference) {
            Intent Intent = new Intent(MainActivity.this, ActivityPreferences.class);
            startActivityForResult(Intent, cPreferencesCode);
        } else if (id == R.id.action_exit) {
            System.exit(0);        }

        return super.onOptionsItemSelected(item);
    }

    public void btnSendOnClick(View view) {
        String str = edtSend.getText().toString();
        sockClient.Send(str.getBytes());
    }

    private GravityListener GravityMotorDC  = new GravityListener() {
        public int MaxGravity = 10, MaxValue = 1023;

        @Override
        public void doEvent(int aX, int aY) {
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
        prefPort        = Integer.parseInt(Value);

        prefServer      = sharedPreferences.getString("preference_server_address", "192.168.4.1");
        prefGravityBind = sharedPreferences.getBoolean("preference_gravitybind", false);
    }

    @Override
    // called from startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cPreferencesCode) {
            LoadPreferences();
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
