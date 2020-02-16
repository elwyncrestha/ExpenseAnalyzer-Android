package com.elvin.expense_analyzer.ui.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
public class GyroListener implements SensorEventListener {
    private Context context;
    private OnGyroChange onGyroChange;
    private SensorManager sensorManager;

    public GyroListener(Context context) {
        this.context = context;
        resume();
    }

    public void setOnGyroChange(OnGyroChange onGyroChange) {
        this.onGyroChange = onGyroChange;
    }

    public void resume() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            throw new UnsupportedOperationException("Sensors not supported");
        }
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(context, "Gyroscope not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[1] < -9) {
            onGyroChange.onChange(true);
        } else if (event.values[1] > 9) {
            onGyroChange.onChange(false);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface OnGyroChange {
        void onChange(boolean leftTurn);
    }
}
