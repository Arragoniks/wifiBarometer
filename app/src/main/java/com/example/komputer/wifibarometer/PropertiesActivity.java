package com.example.komputer.wifibarometer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class PropertiesActivity extends AppCompatActivity {

    private String resetGraph = "1";
    private String buildGraph = "1";
    private String saveBuffer = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties);
        Button buttonApply = findViewById(R.id.buttonPropApply);
        Button buttonROK = findViewById(R.id.buttonROK);
        Button buttonRNO = findViewById(R.id.buttonRNO);
        Button buttonBOK = findViewById(R.id.buttonBOK);
        Button buttonBNO = findViewById(R.id.buttonBNO);
        Button buttonSOK = findViewById(R.id.buttonSOK);
        Button buttonSNO = findViewById(R.id.buttonSNO);
        final Spinner spinnerSamples = findViewById(R.id.spinnerSamples);
        final Spinner spinnerPoints = findViewById(R.id.spinnerPoints);

        buttonROK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGraph = "1";
            }
        });

        buttonRNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGraph = "0";
            }
        });

        buttonBOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildGraph = "1";
            }
        });

        buttonBNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildGraph = "0";
            }
        });

        buttonSOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBuffer = "1";
            }
        });

        buttonSNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBuffer = "0";
            }
        });

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String samples = spinnerSamples.getSelectedItem().toString();
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
