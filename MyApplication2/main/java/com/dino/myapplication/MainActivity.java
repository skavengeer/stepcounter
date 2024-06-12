package com.dino.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView stepViewStepcounter, stepDetector;
    private SensorManager sensorManager;
    private Sensor mStepcounter;
    private Button button;
    private boolean isSensor;
    boolean run, sD = false;
    int stepC,stepC1 = 0;

    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stepViewStepcounter = findViewById(R.id.step);
        button = findViewById(R.id.button);
        stepDetector = findViewById(R.id.stepDet);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        View.OnClickListener ocButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sD = false;
               stepViewStepcounter.setText("0");
            }
        };
        
        if (!checkPermission()) requestPermissions();
        
        button.setOnClickListener(ocButton);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACTIVITY_RECOGNITION
        }, REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermission() {
        int device_result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION);
        return  device_result == PackageManager.PERMISSION_GRANTED  ;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (run) {
            stepC = (int) event.values[0];
            if (!sD) {         //sD false   първо минаване
                if (stepC > 0) {
                    stepC1 = stepC;
                    stepC = stepC1 - stepC;
                }
                sD = true;
            } else          // не е първо минаване
                stepC = stepC - stepC1;
            stepViewStepcounter.setText(String.valueOf(stepC));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        run = true;
        mStepcounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if ( mStepcounter!= null){
            sensorManager.registerListener(this, mStepcounter, SensorManager.SENSOR_DELAY_NORMAL);
            isSensor = true;
        }else {
            stepViewStepcounter.setText("Not sensor counter ");
            isSensor = false;
        }
  }

    @Override
    protected void onPause() {
        super.onPause();
        run = false;
        if (isSensor )
            sensorManager.unregisterListener(this, mStepcounter );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                break;
        }
    }
}