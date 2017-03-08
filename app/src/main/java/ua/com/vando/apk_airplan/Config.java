package ua.com.vando.apk_airplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.widget.TextView;

public class Config{
    Activity activity;
    SharedPreferences sPrefs;

    public Config (Activity aActivity) {
        activity = aActivity;
        sPrefs = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public String LoadStr(String aKey, String aDef) {
        return sPrefs.getString(aKey, aDef);
    }

    void SaveStr(String aKey, String aValue) {
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putString(aKey, aValue);
        editor.commit();
    }

    void LoadView(TextView aTextView, String aKey, String aDef) {
        String Str = LoadStr(aKey, aDef);
        aTextView.setText(Str);
    }

    void SaveView(TextView aTextView, String aKey) {
        String Str =  aTextView.getText().toString();
        //String aKey = aTextView.getResources().getResourceName(aTextView.getId());
        SaveStr(aKey, Str);
    }

}
