package com.example.komputer.wifibarometer;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Timer;
import java.util.TimerTask;

public class MeasureActivity extends AppCompatActivity {

    String url = "http://192.168.4.1/";
    boolean measuring = false;
    Button buttonS;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    Timer myTimer;
    long startMillis;

    int samples = 1000;
    int resetGraph = 1;
    int buildGraph = 1;
    int pointsGraph = 10000;
    int saveToBuffer = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        //FileHandler fileHandler = new FileHandler();
        //fileHandler.readFile("/storage/sdcard0/Android/data/com.example.komputer.wifibarometer/files/outputfile.txt", "request", this);

        buttonS = findViewById(R.id.buttonS);
        if(buildGraph == 1) {
            graph = (GraphView) findViewById(R.id.graph);
            graph.getViewport().setScrollable(true); // enables horizontal scrolling
            graph.getViewport().setScrollableY(true); // enables vertical scrolling
            graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
            graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        }


        buttonS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measuring = !measuring;
                if(measuring){
                    if(resetGraph == 1){
                        graph.removeAllSeries();
                    }
                    buttonS.setText("stop");
                    start();
                }else{
                    myTimer.cancel();
                    buttonS.setText("start");
                    onfinish(SystemClock.elapsedRealtime() - startMillis);
                }
            }
        });
    }

    private void start(){
        DataPoint[] dataPoints = {};
        if(buildGraph == 1){
            series = new LineGraphSeries<>(dataPoints);
            graph.addSeries(series);
        }
        myTimer = new Timer();
        startMillis = SystemClock.elapsedRealtime();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                measure();
            }
        }, 0L, (long)samples);
    }

    private void measure(){
        long currentMillis = SystemClock.elapsedRealtime() - startMillis;
        Integer pressure = 0;
        DownloadTask downloadTask = new DownloadTask();
        if(downloadTask != null) {
            downloadTask.execute(url);
            try {
                pressure = downloadTask.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(buildGraph == 1){
            series.appendData(new DataPoint(currentMillis, pressure), true, pointsGraph);
        }
        if(saveToBuffer == 1){
            //writeToFile(pressure.toString() + " " + currentMillis, this);
        }
    }

    private void onfinish(long totalMillis){
        if(buildGraph == 1) {
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(totalMillis);
        }
    }
}
