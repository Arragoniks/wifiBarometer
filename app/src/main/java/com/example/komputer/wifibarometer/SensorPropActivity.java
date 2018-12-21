package com.example.komputer.wifibarometer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.concurrent.ExecutionException;

public class SensorPropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_prop);

        final Spinner spinnerPr = findViewById(R.id.spinnerPr);
        final Spinner spinnerInter = findViewById(R.id.spinnerInter);
        final Spinner spinnerFilter = findViewById(R.id.spinnerFilter);
        Button buttonApply = findViewById(R.id.buttonASP);

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pressure = spinnerPr.getSelectedItem().toString();
                String interval = spinnerInter.getSelectedItem().toString();
                String filter = spinnerFilter.getSelectedItem().toString();
                String request = pressure + "," + interval + "," + filter;
                Log.e("RequestProperties", request);
                FileHandler fileHandler = new FileHandler();
                //fileHandler.editProperties("sensorProp", request,SensorPropActivity.this);
                String url = fileHandler.readProperties("url", SensorPropActivity.this);
                sendProp(pressure, interval, filter, url);
                Intent intent = new Intent(SensorPropActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }

    private void sendProp(String pressure, String interval, String filter, String url){
        String urlrequest;
        DownloadTask downloadTask = new DownloadTask();
        try {
            DownloadTask downloadTask1 = new DownloadTask();
            urlrequest = url + "?baro=" + pressure;
            downloadTask1.execute(urlrequest);
            downloadTask1.get();
            DownloadTask downloadTask2 = new DownloadTask();
            urlrequest = url + "?inter=" + interval;
            downloadTask2.execute(urlrequest);
            downloadTask2.get();
            DownloadTask downloadTask3 = new DownloadTask();
            urlrequest = url + "?filter=" + filter;
            downloadTask3.execute(urlrequest);
            downloadTask3.get();
            DownloadTask downloadTask4 = new DownloadTask();
            urlrequest = url + "?apply=prop";
            downloadTask4.execute(urlrequest);
            downloadTask4.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
