package com.example.komputer.wifibarometer.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.komputer.wifibarometer.R;
import com.example.komputer.wifibarometer.util.FileHandler;
import com.example.komputer.wifibarometer.util.TCPRequestTask;

import java.util.HashMap;
import java.util.Map;

public class SettingsDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener{

    Map<String, String> settings;

    public SettingsDialog(Context context){
        super(context);

        FileHandler fileHandler = new FileHandler();
        settings = fileHandler.readSettings();

        LinearLayout linearLayout = createLinearLayout(context);
        TextView textViewPressure = createTextView(context, "Pressure (Accuracy)");
        TextView textViewInterval = createTextView(context, "Interval (ms)");
        TextView textViewFilter = createTextView(context, "Filter (Coefficient)");
        Spinner spinnerPressure = createSpinner(context,
                context.getResources().getStringArray(R.array.pressure),
                settings.get("pressure"), "pressure");
        Spinner spinnerInterval = createSpinner(context,
                context.getResources().getStringArray(R.array.interval),
                settings.get("interval"), "interval");
        Spinner spinnerFilter = createSpinner(context,
                context.getResources().getStringArray(R.array.filter),
                settings.get("filter"), "filter");

        linearLayout.addView(textViewPressure);
        linearLayout.addView(spinnerPressure);
        linearLayout.addView(textViewInterval);
        linearLayout.addView(spinnerInterval);
        linearLayout.addView(textViewFilter);
        linearLayout.addView(spinnerFilter);

        setView(linearLayout);//connect a layout resource file
        setTitle("Settings");
        setPositiveButton(R.string.ok, this);
        setNeutralButton(R.string.cancel, null);
    }

    private Spinner createSpinner(Context context, String[] items, String val, final String key){
        Spinner spinner = new Spinner(context);
        spinner.setAdapter(new ArrayAdapter<>(context,
                R.layout.support_simple_spinner_dropdown_item,
                items));
        spinner.setPadding(15, 0, 0, 0);
        for(int i = 0; i < items.length; i++){
            if(items[i].equals(val)){
                spinner.setSelection(i);
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settings.put(key, parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return spinner;
    }

    private TextView createTextView(Context context, String text){
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(15, 0, 0, 0);
        textView.setTextSize(20);
        textView.setText(text);
        return textView;
    }

    private LinearLayout createLinearLayout(Context context){
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String interval = "";
        switch(settings.get("interval")){
            case "63":
                interval = "1";
                break;
            case "125":
                interval = "2";
                break;
            case "250":
                interval = "3";
                break;
            case "500":
                interval = "4";
                break;
            case "1000":
                interval = "5";
                break;
            case "10000":
                interval = "6";
                break;
            case "20000":
                interval = "7";
                break;
        }
//        String[] request = new String[2];
//        request[0] = settings.get("url");
        String request = "settings"+" "
                +"pressure="+settings.get("pressure")+" "
                +"interval="+interval+" "
                +"filter="+settings.get("filter")+" "
                +"temperature="+settings.get("temperature")+" "
                +"humidity="+settings.get("humidity")+" "
                +"maxpackage="+settings.get("maxpackage");
        TCPRequestTask tcpRequestTask = new TCPRequestTask();
        tcpRequestTask.execute(request);
        Log.e("URL", settings.get("url"));
        FileHandler fileHandler = new FileHandler();
        fileHandler.editSettings(settings);
    }

}
