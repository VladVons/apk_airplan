package ua.com.vando.apk_airplan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.hrules.horizontalnumberpicker.HorizontalNumberPickerListener;


public class ActivityDialogMotorDC extends AppCompatActivity implements HorizontalNumberPickerListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_motor_dc);
    }

    @Override
    public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker, int value) {

    }
}
