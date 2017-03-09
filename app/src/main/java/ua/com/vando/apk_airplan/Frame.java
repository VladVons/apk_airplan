package ua.com.vando.apk_airplan;

import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CheckBox;


class FrmBase {
    protected TextView TextView1;
    protected Activity Activity1;

    public FrmBase (Activity aActivity, int aTextViewID) {
        Activity1 = aActivity;
        TextView1 = (TextView) Activity1.findViewById(aTextViewID);
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
    private static int cMaxValue = 1023;

    public FrmMotorDC (Activity aActivity, int aTextViewID, int aSeekBarID) {
        super(aActivity, aTextViewID, aSeekBarID);

        SeekBar1.setMax(cMaxValue);
        TextView1.setText("Max =" + String.valueOf(cMaxValue));
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
    }
}
