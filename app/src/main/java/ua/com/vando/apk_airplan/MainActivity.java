package ua.com.vando.apk_airplan;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    TextView txtvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtvHello = (TextView) findViewById(R.id.txtv_hello);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v("onOptionsItemSelected 1", "onOptionsItemSelected 2");
        int id = item.getItemId();

        if (id == R.id.action_client) {
            Intent Intent = new Intent(MainActivity.this, ClientActivity.class);
            startActivity(Intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnClientOnClick(View view) {
        txtvHello.setText("ClientActivity");

        Intent Intent = new Intent(MainActivity.this, ClientActivity.class);
        startActivity(Intent);
    }

    public void btnServerOnClick(View view) {
        txtvHello.setText("ServerActivity");
        Intent Intent = new Intent(MainActivity.this, ServerActivity.class);
        startActivity(Intent);
    }

    public void btnInfoOnClick(View view) {
        //String Ip = "xxx.xxx.xxx.xxx";
        String Str;

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wm.getConnectionInfo().getIpAddress();
        if (ipAddress == 0) {
            Str = "No connection";
        }else {
            Str = String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        }

        txtvHello.setText("IP: " + Str);
    }
 }