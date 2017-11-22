package com.umarfadil.pedometersdk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.motion.SmotionPedometer;

public class MainActivity extends AppCompatActivity implements PedometerCallback {

    PedometerHelper pedometerHelper;
    TextView textCalorie;
    TextView textDistance;
    TextView textSpeed;
    TextView textCount;
    TextView textStatus;
    Spinner spinMode;
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pedometerHelper = new PedometerHelper(this);

        try {
            pedometerHelper.initialize();
            pedometerHelper.setPedometerCallback(this);

        }catch (IllegalArgumentException e){
            showErrorDialog("Something went wrong",e.getMessage());
            return;
        }catch (SsdkUnsupportedException e){
            showErrorDialog("SDK Not Supported",e.getMessage());
            return;
        }

        textCalorie = (TextView)findViewById(R.id.text_calorie);
        textDistance = (TextView)findViewById(R.id.text_distance);
        textSpeed = (TextView)findViewById(R.id.text_speed);
        textCount = (TextView)findViewById(R.id.text_count);
        textStatus = (TextView)findViewById(R.id.text_status);

        spinMode = (Spinner)findViewById(R.id.spinner_menu);

        spinMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case PedometerHelper.MODE_PEDOMETER_REALTIME:
                        pedometerHelper.setModePedometer(PedometerHelper.MODE_PEDOMETER_REALTIME);
                        break;
                    case PedometerHelper.MODE_PEDOMETER_PERIODIC:
                        pedometerHelper.setModePedometer(PedometerHelper.MODE_PEDOMETER_PERIODIC);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        btnStart = (Button)findViewById(R.id.btn_start);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pedometerHelper.isStarted() == false){
                    pedometerHelper.start();
                }
                else {
                    pedometerHelper.stop();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop pedometer
        pedometerHelper.stop();
    }

    private String getStatus(int status) {
        String str = null;
        switch (status) {
            case SmotionPedometer.Info.STATUS_WALK_UP:
                str = "Walk Up";
                break;
            case SmotionPedometer.Info.STATUS_WALK_DOWN:
                str = "Walk Down";
                break;
            case SmotionPedometer.Info.STATUS_WALK_FLAT:
                str = "Walk";
                break;
            case SmotionPedometer.Info.STATUS_RUN_DOWN:
                str = "Run Down";
                break;
            case SmotionPedometer.Info.STATUS_RUN_UP:
                str = "Run Up";
                break;
            case SmotionPedometer.Info.STATUS_RUN_FLAT:
                str = "Run";
                break;
            case SmotionPedometer.Info.STATUS_STOP:
                str = "Stop";
                break;
            case SmotionPedometer.Info.STATUS_UNKNOWN:
                str = "Unknown";
                break;
            default:
                break;
        }
        return str;
    }

    @Override
    public void motionStarted() {
        btnStart.setText(R.string.stop);
        enableSpinner(false);
    }

    @Override
    public void motionStopped() {
        btnStart.setText(R.string.start);
        enableSpinner(true);
    }

    @Override
    public void updateInfo(SmotionPedometer.Info info) {
        SmotionPedometer.Info pedometerInfo = info;
        System.out.println("HelloMotion PedometerHelper");
        double calorie = info.getCalorie();
        double distance = info.getDistance();
        double speed = info.getSpeed();
        long count = info.getCount(SmotionPedometer.Info.COUNT_TOTAL);
        int status = info.getStatus();

        textCalorie.setText(String.valueOf(calorie));
        textDistance.setText(String.valueOf(distance));
        textSpeed.setText(String.valueOf(speed));
        textCount.setText(String.valueOf(count));
        textStatus.setText(getStatus(status));

        Log.d("Burn", "onChanged: "+ " calorie "+calorie+
                " distance"+distance+
                " speed"+speed+
                " count"+count+
                " status"+status);
    }

    void showErrorDialog(String title,String message){

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    void enableSpinner(boolean enabled){
        spinMode.setEnabled(enabled);
    }
}
