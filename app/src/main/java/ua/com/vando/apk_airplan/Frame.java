package ua.com.vando.apk_airplan;

import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


class FrmBase {
    protected TextView TextView1;
    protected Activity Activity1;
    protected SockClient sockClient = null;
    protected int PinA, PinB;

    public FrmBase (Activity aActivity, int aTextViewID) {
        Activity1 = aActivity;
        TextView1 = (TextView) Activity1.findViewById(aTextViewID);
    }

    public void Send () {
        if (sockClient != null) {
            sockClient.Send();
        }
    }

    public void Init(int aPin, SockClient aSockClient) {
        sockClient = aSockClient;
        PinA = aPin;
    }
}

class FrmLamp extends FrmBase implements CheckBox.OnClickListener{
    protected CheckBox CheckBox1;

    public FrmLamp (Activity aActivity, int aTextViewID, int aCheckBoxID) {
        super(aActivity, aTextViewID);

        CheckBox1 = (CheckBox) Activity1.findViewById(aCheckBoxID);
        CheckBox1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean Checked = ((CheckBox) v).isChecked();
        TextView1.setText(String.valueOf(Checked));

        sockClient.Clear();
        sockClient.SetPwmOff(PinA);
        sockClient.SetPin(PinA, Checked ? 1 : 0);
        sockClient.Send();
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
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}


class FrmMotorDC extends FrmMotorBase {
    private int Min, Max;
    private int Reverse, LastSpeed;

    public FrmMotorDC (Activity aActivity, int aTextViewID, int aSeekBarID) {
        super(aActivity, aTextViewID, aSeekBarID);
        SetReverse(false);
        SetRange(0, 999);
    }

    public void SetRange(int aMin, int aMax) {
        Min = aMin;
        Max = aMax;

        SeekBar1.setMax(aMax);
        TextView1.setText("Max =" + String.valueOf(Max));
    }

    public void Init(int aPinA, int aPinB, SockClient aSockClient) {
        sockClient = aSockClient;
        PinA = aPinA;
        PinB = aPinB;
    }

    public void SetReverse(boolean aValue) {
        Reverse = aValue ? -1: 1;
        SetSpin(LastSpeed * Reverse);
    }

    public void SetSpin(int aValue) {
        if (sockClient == null) return;

        int Pin_A, Pin_B;
        if (aValue > 0) {
            Pin_A = PinA;
            Pin_B = PinB;
        }else{
            Pin_A = PinB;
            Pin_B = PinA;
        }

        int Speed = Math.abs(aValue);
        if (Speed > 999)
            Speed = 999;
        if (Speed == 0)
            Speed = 1;
        LastSpeed = Speed;


        sockClient.Clear();
        sockClient.SetPwmOff(Pin_B);
        sockClient.SetPwmFreq(Pin_A, 100);
        sockClient.SetPwmDuty(Pin_A, Speed);
        sockClient.SetPin(Pin_B, 0);
        sockClient.SetPin(Pin_A, 1);
        sockClient.Send();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        SetSpin(progress * Reverse);
    }
}

