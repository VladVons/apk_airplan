package ua.com.vando.apk_airplan;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;


class FrmBase {
    protected TextView TextView1;
    protected Activity Activity1;
    protected SockClient sockClient = null;
    protected JSONObject json;

    public FrmBase (Activity aActivity, int aTextViewID) {
        Activity1 = aActivity;
        TextView1 = (TextView) Activity1.findViewById(aTextViewID);
        json = new JSONObject();
    }

    public void Send () {
        if (sockClient != null) {
            sockClient.Send(json.toString().getBytes());
        }
    }

    public void Init(int aNo, SockClient aSockClient) {
        sockClient = aSockClient;

        try {
            json.put("No", aNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

class FrmMotorBase extends FrmBase implements SeekBar.OnSeekBarChangeListener{
    protected SeekBar SeekBar1;

    public FrmMotorBase (Activity aActivity, int aTextViewID, int aSeekBarID) {
        super(aActivity, aTextViewID);

        SeekBar1  = (SeekBar)  Activity1.findViewById(aSeekBarID);
        SeekBar1.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TextView1.setText(String.valueOf(progress));

        try {
            json.put("Value", progress);
            Send ();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}


class FrmMotorDC extends FrmMotorBase {
    private static int cMaxValue = 1023;

    public FrmMotorDC (Activity aActivity, int aTextViewID, int aSeekBarID) {
        super(aActivity, aTextViewID, aSeekBarID);

        SeekBar1.setMax(cMaxValue);
        TextView1.setText("Max =" + String.valueOf(cMaxValue));

        try {
            json.put("Name", "MotorDC");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


class FrmLamp extends FrmBase implements CheckBox.OnClickListener{
    protected CheckBox CheckBox1;

    public FrmLamp (Activity aActivity, int aTextViewID, int aCheckBoxID) {
        super(aActivity, aTextViewID);

        CheckBox1 = (CheckBox) Activity1.findViewById(aCheckBoxID);
        CheckBox1.setOnClickListener(this);

        try {
            json.put("Name", "Lamp");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        boolean Checked = ((CheckBox) v).isChecked();
        TextView1.setText(String.valueOf(Checked));

        try {
            json.put("Value", Checked ? 1 : 0);
            Send ();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
