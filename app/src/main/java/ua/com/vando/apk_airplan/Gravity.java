package ua.com.vando.apk_airplan;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//
import java.util.ArrayList;
import java.util.List;

////https://github.com/akexorcist/Android-Sensor-Gyroscope/blob/master/src/app/akexorcist/sensor_gyroscope/Main.java

interface GravityListener {
    public void doEvent(int aX, int aY);
}


public class Gravity {
    private List gravityListener = new ArrayList();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int prevAX, prevAY;
    public  int Accuracy = 1;


    public Gravity (Activity aActivity) {
        mSensorManager = (SensorManager) aActivity.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void Start() {
        mSensorManager.registerListener(Listener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void Stop() {
        mSensorManager.unregisterListener(Listener);
    }

    public void registerListener(GravityListener aGravityListener) {
        gravityListener.add(aGravityListener);
    }

    private SensorEventListener Listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int AX = Math.round(event.values[0] * Accuracy);
            int AY = Math.round(event.values[1] * Accuracy);

            if (prevAX != AX || prevAY != AY) {
                prevAX = AX;
                prevAY = AY;

                for (Object name : gravityListener) {
                    ((GravityListener)name).doEvent(AX, AY);
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
