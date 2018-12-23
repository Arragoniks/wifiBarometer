package com.example.komputer.wifibarometer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

public class PropertiesActivity extends AppCompatActivity {

    private String resetGraph = "1";
    private String buildGraph = "1";
    private String saveBuffer = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties);

        FileHandler fileHandler = new FileHandler();
        resetGraph = fileHandler.readProperties("resetGraph", this);
        buildGraph = fileHandler.readProperties("buildGraph", this);
        saveBuffer = fileHandler.readProperties("saveBuffer", this);

        Button buttonApply = findViewById(R.id.buttonPropApply);

        final Spinner spinnerSamples = findViewById(R.id.spinnerSamples);
        final Spinner spinnerPoints = findViewById(R.id.spinnerPoints);
        final EditText editText = findViewById(R.id.editSamples);

        Switch switch1 = findViewById(R.id.switch1);
        Switch switch2 = findViewById(R.id.switch2);
        Switch switch3 = findViewById(R.id.switch3);

        if(resetGraph.equals("1")){
            switch1.setChecked(true);
        }else{
            switch1.setChecked(false);
        }
        if(buildGraph.equals("1")){
            switch2.setChecked(true);
        }else{
            switch2.setChecked(false);
        }
        if(saveBuffer.equals("1")){
            switch3.setChecked(true);
        }else{
            switch3.setChecked(false);
        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    resetGraph = "1";
                }else{
                    resetGraph = "0";
                }
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    buildGraph = "1";
                }else{
                    buildGraph = "0";
                }
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    saveBuffer = "1";
                }else{
                    saveBuffer = "0";
                }
            }
        });

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String samples;
                String editsamples = editText.getText().toString();
                if(!editsamples.equals("")){
                    samples = editsamples;
                }else{
                    samples = spinnerSamples.getSelectedItem().toString();
                }
                String points = spinnerPoints.getSelectedItem().toString();
                FileHandler fileHandler = new FileHandler();
                fileHandler.editProperties("samples", samples, PropertiesActivity.this);
                fileHandler.editProperties("pointsGraph", points, PropertiesActivity.this);
                fileHandler.editProperties("resetGraph", resetGraph, PropertiesActivity.this);
                fileHandler.editProperties("buildGraph", buildGraph, PropertiesActivity.this);
                fileHandler.editProperties("saveBuffer", saveBuffer, PropertiesActivity.this);
                Intent intent = new Intent(PropertiesActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }
}
