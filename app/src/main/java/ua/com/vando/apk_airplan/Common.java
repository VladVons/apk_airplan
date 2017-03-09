package ua.com.vando.apk_airplan;

import android.app.Activity;
import android.net.wifi.WifiManager;

import java.util.Locale;

import static android.content.Context.WIFI_SERVICE;

class Net {
    static public String GetOwnIP(Activity aActivity) {
        String Result  = "";

        WifiManager wm = (WifiManager) aActivity.getSystemService(WIFI_SERVICE);
        int ipAddress = wm.getConnectionInfo().getIpAddress();
        if (ipAddress != 0) {
            Result = String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        }

        return Result;
    }
}