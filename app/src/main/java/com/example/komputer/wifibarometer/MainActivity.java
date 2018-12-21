package com.example.komputer.wifibarometer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner spinnerMenu = findViewById(R.id.spinnerMenuM);

        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String choise = spinnerMenu.getSelectedItem().toString();
                Intent intent = null;
                switch(choise){
                    case "Measurement":
                        intent = new Intent(MainActivity.this, GraphingActivity.class);
                        break;
                    case "Sensor Properties":
                        intent = new Intent(MainActivity.this, SensorPropActivity.class);
                        break;
                    case "Properties":
                        intent = new Intent(MainActivity.this, PropertiesActivity.class);
                        break;
                }
                if(!choise.equals("Main")) {
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}

