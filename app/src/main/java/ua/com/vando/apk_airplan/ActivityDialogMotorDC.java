package ua.com.vando.apk_airplan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.hrules.horizontalnumberpicker.HorizontalNumberPickerListener;


public class ActivityDialogMotorDC extends AppCompatActivity implements HorizontalNumberPickerListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_motor_dc);

        HorizontalNumberPicker HorizontalNumberPicker1   = (HorizontalNumberPicker) findViewById(R.id.horizontal_number_picker1);
        HorizontalNumberPicker1.setMinValue(-5);
        HorizontalNumberPicker1.setMaxValue(5);
        HorizontalNumberPicker1.setListener(this);
    }

    @Override
    public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker, int value) {
        //Log.d("current value: " + Integer.toString(value));
        Log.d("current value:", String.valueOf(value));
    }
}
