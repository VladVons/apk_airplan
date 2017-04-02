package ua.com.vando.apk_airplan;

import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CheckBox;

class FrmBase {
    protected TextView TextView1;
    protected Activity Activity1;
    protected SockClient sockClient = null;
    protected int PinA, PinB;

    public FrmBase (Activity aActivity, int aTextViewID) {
        Activity1 = aActivity;
        TextView1 = (TextView) Activity1.findViewById(aTextViewID);
    }

    public void Send (Serial aSerial) {
        if (sockClient != null) {
            sockClient.Send(aSerial);
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

        Serial serial = new Serial();
        serial.SetPwmOff(PinA);
        serial.SetPin(PinA, Checked ? 1 : 0);
        Send(serial);
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

    public void Init(int aPinA, int aPinB, SockClient aSockClient) {
        sockClient = aSockClient;
        PinA = aPinA;
        PinB = aPinB;
    }

    public void SetRange(int aMin, int aMax) {
        Min = aMin;
        Max = aMax;

        SeekBar1.setMax(aMax);
        TextView1.setText("Max =" + String.valueOf(Max));
    }

    public void Stop() {
        TextView1.setText("Stop");

        Serial serial = new Serial();
        serial.SetPwmOffArr(new int[] {PinA, PinB});
        serial.SetPinArr(new int[] {PinA, PinB}, 1);
        Send(serial);
    }

    public void Start() {
        TextView1.setText("Run");
        SetSpin(LastSpeed * Reverse);
    }

    public void Start(boolean aStart) {
        if (aStart)
            Start();
        else
            Stop();
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


        Serial serial = new Serial();
        serial.SetPwmOff(Pin_B);
        serial.SetPin(Pin_B, 0);

        serial.SetPwmFreq(Pin_A, 100);
        serial.SetPwmDuty(Pin_A, Speed);
        serial.SetPin(Pin_A, 1);

        Send(serial);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        SetSpin(progress * Reverse);
    }
}

