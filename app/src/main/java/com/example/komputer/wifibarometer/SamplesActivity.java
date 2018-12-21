package com.example.komputer.wifibarometer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SamplesActivity extends AppCompatActivity {

    Spinner spinnerSamples;
    Spinner spinnerMeasuring;
    Button buttonOK;
    EditText editSamples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samples);
        buttonOK = findViewById(R.id.buttonOK);
        editSamples = findViewById(R.id.editSamples);
        spinnerSamples = findViewById(R.id.spinnerSamples);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String res = editSamples.getText().toString();
                if(res.equals("")){
                    res = spinnerSamples.getSelectedItem().toString();
                }
                if(res.equals("")){
                    buttonOK.setError("Choose samples");
                }else{
                    int num = Integer.parseInt(res);
                    //int measuring = Integer.parseInt(spinnerMeasuring.getSelectedItem().toString());
                    Intent intent = new Intent(SamplesActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("SAMPLES", num);
                    //intent.putExtra("MEASURING", measuring);
                    startActivity(intent);
                }
            }
        });


    }
}
