package com.example.komputer.wifibarometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;

public class GraphActivity extends AppCompatActivity {

    Button buttonGS;
    Button buttonSave;
    int samples = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        buttonGS = findViewById(R.id.buttonGS);
        buttonSave = findViewById(R.id.buttonSave);

        samples = Integer.parseInt(getIntent().getStringExtra("SAMPLES"));


        if(samples == 0){
            //toGraph button
        }else{
            //start graph
        }
    }
}
