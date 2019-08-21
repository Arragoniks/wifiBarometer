package com.example.komputer.wifibarometer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.komputer.wifibarometer.dialog.ConfirmDialog;
import com.example.komputer.wifibarometer.util.FileHandler;
import com.example.komputer.wifibarometer.activity.MainActivity;
import com.example.komputer.wifibarometer.dialog.PathSelectDialog;
import com.example.komputer.wifibarometer.dialog.SettingsDialog;
import com.example.komputer.wifibarometer.R;
import com.example.komputer.wifibarometer.util.TCPRequestTask;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;
import java.util.Map;

public class FragmentGraphing extends Fragment implements View.OnClickListener{

    private Button buttonS;

    private GraphView graph;

    private Handler myHandler;
    private MyRunnable myRunnable;

    private boolean measuring = false;
    private Map<String, String> settings;
    private LineGraphSeries<DataPoint> series;
    private FileHandler fileHandler;
    private String requestData;
    private String requestStart;
    private String requestStop;
    private String requestConnect;

    private int pointNumber;
    private long period;

    private String fileName;
    private String defaultFileName;
    private String fileExtension;

    private class MyRunnable implements Runnable{
        @Override
        public void run() {
            myHandler.postDelayed(this, period);
            measure();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graphing, container, false);

        graph = rootView.findViewById(R.id.graph);
        buttonS = rootView.findViewById(R.id.buttonS);

        setHasOptionsMenu(true);

        init();//move init from view init to start graphing

        buttonS.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!measuring)
            inflater.inflate(R.menu.app_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!measuring) {
            int id = item.getItemId();
            switch (id) {
                case R.id.newFile:
                    Log.e("New File", "menu");
                    newFile();
                    return true;
                case R.id.open:
                    openFile();
                    return true;
                case R.id.save:
                    Log.e("Save", "menu");
                    saveFile();
                    return true;
                case R.id.settings:
                    Log.e("Settings", "menu");
                    openSettings();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

//    private String[] initRequest(String request){
//        String[] array = {settings.get("url"), settings.get("port"), request};
//        return array;
//    }

    private void init(){
        fileHandler = new FileHandler();
        settings = fileHandler.readSettings();

        myHandler = new Handler();
        myRunnable = new MyRunnable();

        TCPRequestTask.setValues(settings.get("url"),
                Integer.parseInt(settings.get("port")),
                Integer.parseInt(settings.get("connect")),
                Integer.parseInt(settings.get("receive")));

        FileHandler.setExtension(settings.get("extension"));

        fileName = settings.get("newfile");
        defaultFileName = settings.get("newfile");
        fileExtension = settings.get("extension");

        period = Long.parseLong(settings.get("interval")) *
                Long.parseLong(settings.get("packagecount"));
        pointNumber = 0;

//        String url = settings.get("url");
//        String port = settings.get("port");
        requestData = "data";
        requestStart = "start";
        requestStop = "stop";
        requestConnect = "connect";
        /*
        requestData[0] = url;
        requestStart[0] = url;
        requestStop[0] = url;
        requestConnect[0] = url;
        requestData[1] = port;
        requestStart[1] = port;
        requestStop[1] = port;
        requestConnect[1] = port;
        requestData[2] = "data";
        requestStart[2] = "start";
        requestStop[2] = "stop";
        requestConnect[2] = "connect";
        */

        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
    }

    private void newFile(){
        if(!graph.getSeries().isEmpty() && fileName.equals(defaultFileName)){
            ConfirmDialog confirmDialog = new ConfirmDialog(getContext(),
                    "Save current graph?",
                    "The new graph will replace the current one!");
            confirmDialog.setConfirmDialogListener(new ConfirmDialog.ConfirmDialogListener() {
                @Override
                public void onSelected(boolean button) {
                    if(button){
                        PathSelectDialog pathSelectDialog = new PathSelectDialog(getContext(), 1, fileExtension);
//                        pathSelectDialog.setFilter(searchFilter);
                        pathSelectDialog.setOpenDialogListener(new PathSelectDialog.PathSelectListener() {
                            @Override
                            public void onSelectedFile(String filePath) {
                                fileName = defaultFileName;
                                MainActivity.setTileForBar(fileName);
                                graph.removeAllSeries();
//                                fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
//                                MainActivity.setTileForBar(fileName);
                                Toast.makeText(getContext(), filePath, Toast.LENGTH_SHORT).show();
                                FileHandler fileHandler = new FileHandler();
                                fileHandler.saveBufferInFile(filePath);
                            }
                        });
                        pathSelectDialog.show();
                    }else{
                        fileName = defaultFileName;
                        MainActivity.setTileForBar(fileName);
                        graph.removeAllSeries();
                    }
                }
            });
            confirmDialog.show();
        }else{
            fileName = defaultFileName;
            MainActivity.setTileForBar(fileName);
            graph.removeAllSeries();
        }
    }

    private void openFile(){
        if(!graph.getSeries().isEmpty() && fileName.equals(defaultFileName)){
            ConfirmDialog confirmDialog = new ConfirmDialog(getContext(),
                    "Save current graph?",
                    "The new graph will replace the current one!");
            confirmDialog.setConfirmDialogListener(new ConfirmDialog.ConfirmDialogListener() {
                @Override
                public void onSelected(boolean button) {
                    if(button){
                        PathSelectDialog pathSelectDialog = new PathSelectDialog(getContext(), 1, fileExtension);
//                        pathSelectDialog.setFilter(searchFilter);
                        pathSelectDialog.setOpenDialogListener(new PathSelectDialog.PathSelectListener() {
                            @Override
                            public void onSelectedFile(String filePath) {
                                PathSelectDialog pathSelectDialog = new PathSelectDialog(getContext(), 0, fileExtension);
//                                pathSelectDialog.setFilter(searchFilter);
                                pathSelectDialog.setOpenDialogListener(new PathSelectDialog.PathSelectListener() {
                                    @Override
                                    public void onSelectedFile(String filePath) {
                                        fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
                                        MainActivity.setTileForBar(fileName);
                                        buildGraphFromFile(filePath);
//                                        Toast.makeText(getContext(), filePath, Toast.LENGTH_SHORT).show();
//                                        FileHandler fileHandler = new FileHandler();
//                                        fileHandler.saveBufferInFile(filePath);
                                    }
                                });
                                pathSelectDialog.show();
                                fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
                                MainActivity.setTileForBar(fileName);
                                Toast.makeText(getContext(), filePath, Toast.LENGTH_SHORT).show();
                                FileHandler fileHandler = new FileHandler();
                                fileHandler.saveBufferInFile(filePath);
                            }
                        });
                        pathSelectDialog.show();
                    }else{
                        PathSelectDialog pathSelectDialog = new PathSelectDialog(getContext(), 0, fileExtension);
//                        pathSelectDialog.setFilter(searchFilter);
                        pathSelectDialog.setOpenDialogListener(new PathSelectDialog.PathSelectListener() {
                            @Override
                            public void onSelectedFile(String filePath) {
                                fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
                                MainActivity.setTileForBar(fileName);
                                buildGraphFromFile(filePath);
//                                        Toast.makeText(getContext(), filePath, Toast.LENGTH_SHORT).show();
//                                        FileHandler fileHandler = new FileHandler();
//                                        fileHandler.saveBufferInFile(filePath);
                            }
                        });
                        pathSelectDialog.show();
                    }
                }
            });
            confirmDialog.show();
        }else {
            PathSelectDialog pathSelectDialog = new PathSelectDialog(getContext(), 0, fileExtension);
//            pathSelectDialog.setFilter(searchFilter);
            pathSelectDialog.setOpenDialogListener(new PathSelectDialog.PathSelectListener() {
                @Override
                public void onSelectedFile(String filePath) {
                    fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
                    MainActivity.setTileForBar(fileName);
                    buildGraphFromFile(filePath);
                }
            });
            pathSelectDialog.show();
        }
    }

    private void saveFile(){
        if(!graph.getSeries().isEmpty() && fileName.equals(defaultFileName)) {
            PathSelectDialog pathSelectDialog = new PathSelectDialog(getContext(), 1, fileExtension);
//            pathSelectDialog.setFilter(searchFilter);
            pathSelectDialog.setOpenDialogListener(new PathSelectDialog.PathSelectListener() {
                @Override
                public void onSelectedFile(String filePath) {
                    fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
                    MainActivity.setTileForBar(fileName);
                    Toast.makeText(getContext(), filePath, Toast.LENGTH_SHORT).show();
                    FileHandler fileHandler = new FileHandler();
                    fileHandler.saveBufferInFile(filePath);
                }
            });
            pathSelectDialog.show();
        }else{
            Toast.makeText(getContext(),
                    "This graph is already saved",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openSettings(){
        TCPRequestTask tcpRequestTask = new TCPRequestTask();
        tcpRequestTask.execute(requestConnect);
        List<String> list = null;
        try {
            list = tcpRequestTask.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(list != null) {
            SettingsDialog settingsDialog = new SettingsDialog(getContext());
            settingsDialog.show();
        }else{
            Toast.makeText(getContext(),
                    "Check the connection to the ESP",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {

        int buttonId = view.getId();

        if(buttonId == R.id.buttonS) {
            if (!measuring) {
                checkConnection();
            } else {
                stop();
            }
        }
    }

    private void checkConnection(){
        TCPRequestTask tcpRequestTask = new TCPRequestTask();
        tcpRequestTask.execute(requestConnect);
        List<String> list = null;
        try {
            list = tcpRequestTask.get();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(list != null) {
            if(!graph.getSeries().isEmpty() && fileName.equals(defaultFileName)){
                ConfirmDialog confirmDialog = new ConfirmDialog(getContext(),
                        "Save current graph?",
                        "The new graph will replace the current one!");
                confirmDialog.setConfirmDialogListener(new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void onSelected(boolean button) {
                        if(button){
                            PathSelectDialog pathSelectDialog = new PathSelectDialog(getContext(), 1, fileExtension);
//                            pathSelectDialog.setFilter(searchFilter);
                            pathSelectDialog.setOpenDialogListener(new PathSelectDialog.PathSelectListener() {
                                @Override
                                public void onSelectedFile(String filePath) {
                                    fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
                                    MainActivity.setTileForBar(fileName);
                                    Toast.makeText(getContext(), filePath, Toast.LENGTH_SHORT).show();
                                    FileHandler fileHandler = new FileHandler();
                                    fileHandler.saveBufferInFile(filePath);
                                    //start()
                                }
                            });
                            pathSelectDialog.show();
                        }else{
                            start();
                        }
                    }
                });
                confirmDialog.show();
            }else {
                start();
            }
        }else{
            Toast.makeText(getContext(),
                    "Check the connection to the ESP",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void start(){

        measuring = true;
        getActivity().invalidateOptionsMenu();

        Log.e("Log filename", fileName);
        fileName = defaultFileName;
        MainActivity.setTileForBar(fileName);
        graph.removeAllSeries();

        buttonS.setText("stop");

        pointNumber = 0;
        series = new LineGraphSeries<>();
        graph.addSeries(series);

        fileHandler.clearBuffer();

        TCPRequestTask tcpRequestTask = new TCPRequestTask();
        tcpRequestTask.execute(requestStart);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.0);
        graph.getViewport().setMaxX(10.0);
        Log.e("Start", Double.toString(10.0));

        myHandler.postDelayed(myRunnable, period);
    }

    private void stop(){

        measuring = false;

        myHandler.removeCallbacks(myRunnable);

        TCPRequestTask tcpRequestTask = new TCPRequestTask();
        tcpRequestTask.execute(requestStop);

        try{
            List<String> bufferArrayList = tcpRequestTask.get();
            fileHandler.writeBuffer(bufferArrayList);
            String temp;
            int index;
            for(int i = 0; i < bufferArrayList.size(); i++){
                temp = bufferArrayList.get(i);
                index = temp.indexOf(" ");
                series.appendData(new DataPoint(Double.parseDouble(temp.substring(0, index)),
                                Double.parseDouble(temp.substring(index+1, temp.length()))),
                        true, ++pointNumber);
            }
            temp = bufferArrayList.get(bufferArrayList.size()-1);
            index = temp.indexOf(" ");
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
//            graph.getViewport().setMaxX(Long.parseLong(temp.substring(index+1, temp.length())));
        }catch(Exception e){
            e.printStackTrace();
        }

        buttonS.setText("start");
        getActivity().invalidateOptionsMenu();
    }

    private void measure(){

        TCPRequestTask tcpRequestTask = new TCPRequestTask();
        tcpRequestTask.execute(requestData);
        try{
            List<String> bufferArrayList = tcpRequestTask.get();
            fileHandler.writeBuffer(bufferArrayList);
            String temp;
            int index;
            for(int i = 0; i < bufferArrayList.size(); i++){
                temp = bufferArrayList.get(i);
                index = temp.indexOf(" ");
                series.appendData(new DataPoint(Double.parseDouble(temp.substring(0, index)),
                        Double.parseDouble(temp.substring(index+1, temp.length()))),
                        true, ++pointNumber);
            }
        }catch(Exception e){
            e.printStackTrace();
            stop();
            Toast.makeText(getContext(),
                    "The connection with ESP was closed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void buildGraphFromFile(String filePath){

        graph.removeAllSeries();
        pointNumber = 0;
        series = new LineGraphSeries<>();
        graph.addSeries(series);
        try{
            List<String> bufferArrayList = fileHandler.getGraphPointFromFile(filePath);
//            fileHandler.writeBuffer(bufferArrayList);
            String temp;
            int index;
            for(int i = 0; i < bufferArrayList.size(); i++){
                temp = bufferArrayList.get(i);
                index = temp.indexOf(" ");
                series.appendData(new DataPoint(Double.parseDouble(temp.substring(0, index)),
                                Double.parseDouble(temp.substring(index+1, temp.length()))),
                        true, ++pointNumber);
            }
            temp = bufferArrayList.get(bufferArrayList.size()-1);
            index = temp.indexOf(" ");
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
//            graph.getViewport().setMaxX(Long.parseLong(temp.substring(index+1, temp.length())));
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Incorrect file", Toast.LENGTH_SHORT).show();
        }
    }
}
