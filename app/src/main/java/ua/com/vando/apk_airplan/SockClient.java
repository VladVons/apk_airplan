package ua.com.vando.apk_airplan;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class SockClient extends AsyncTask<String , String, Integer> {
    public TextView txtInfo;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        txtInfo.setText("onPreExecute");
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        txtInfo.setText("onPostExecute");
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        txtInfo.setText("onProgressUpdate " + values[0]);
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            TimeUnit.SECONDS.sleep(3);
            publishProgress("FromBKG " + params[0]);
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
