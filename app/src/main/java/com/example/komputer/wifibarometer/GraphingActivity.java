package com.example.komputer.wifibarometer;

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

import java.util.Timer;
import java.util.TimerTask;

public class GraphingActivity extends AppCompatActivity {

    boolean measuring = false;
    Button buttonS;
    Spinner spinnerMenu;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    Timer myTimer;
    long startMillis;
    String url;
    int samples;
    int resetGraph;
    int buildGraph;
    int pointsGraph;
    int saveBuffer;
    FileHandler fileHandler;
    Integer bufferint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphing);
        FileHandler fileHandler = new FileHandler();
        url = fileHandler.readProperties("url", GraphingActivity.this);
        samples = Integer.parseInt(fileHandler.readProperties("samples", GraphingActivity.this));
        resetGraph = Integer.parseInt(fileHandler.readProperties("resetGraph", GraphingActivity.this));
        buildGraph = Integer.parseInt(fileHandler.readProperties("buildGraph", GraphingActivity.this));
        pointsGraph = Integer.parseInt(fileHandler.readProperties("pointsGraph", GraphingActivity.this));
        saveBuffer = Integer.parseInt(fileHandler.readProperties("saveBuffer", GraphingActivity.this));

        graph = (GraphView) findViewById(R.id.graph);

        spinnerMenu = findViewById(R.id.spinnerMenuM);
        buttonS = findViewById(R.id.buttonS);
        if(buildGraph == 1) {
            graph.getViewport().setScrollable(true); // enables horizontal scrolling
            graph.getViewport().setScrollableY(true); // enables vertical scrolling
            graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
            graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        }

        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = spinnerMenu.getSelectedItem().toString();
                switch(selection){
                    case "Main":
                        Intent intent = new Intent(GraphingActivity.this, MainActivity.class);
                        //do intent
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                    case "Reset":
                        graph.removeAllSeries();
                        Log.e("reseting", "reset button");
                        break;
                    case "Save":
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
        if(buildGraph == 1) {
            series = new LineGraphSeries<>(dataPoints);
            graph.addSeries(series);
        }

        fileHandler = new FileHandler();

        if(saveBuffer == 1){
            fileHandler.clearbuffer(GraphingActivity.this);
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
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
        try {
            //if(downloadTask != null)
            long currentMillis = SystemClock.elapsedRealtime() - startMillis;
            bufferint = downloadTask.get();
            if(saveBuffer == 1){
                fileHandler.writeBuffer(bufferint.toString() + " " + currentMillis);
            }
            if(buildGraph == 1) {
                series.appendData(new DataPoint(currentMillis, downloadTask.get()), true, pointsGraph);
            }


        } catch (Exception e) {
            e.printStackTrace();
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
