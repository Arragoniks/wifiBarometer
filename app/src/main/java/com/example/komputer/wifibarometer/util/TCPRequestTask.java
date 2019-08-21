package com.example.komputer.wifibarometer.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.komputer.wifibarometer.activity.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TCPRequestTask extends AsyncTask<String, Void, List<String>> {

    private static String url = "";
    private static int port = 0;
    private static int connect = 0;
    private static int receive = 0;

    public static void setValues(String url1, int port1, int connect1, int receive1){
        url = url1;
        port = port1;
        connect = connect1;
        receive = receive1;
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> response = new ArrayList<>();
        Log.e("TCP", Arrays.toString(strings));
        try {
            String temp;
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(url, port), connect);
            socket.setSoTimeout(receive);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(strings[0]);
            bw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            temp = br.readLine();
            Log.e("Reading", "Start");
            while(temp != null){
                Log.e("TEMP", temp);
                response.add(temp);
                temp = br.readLine();
            }
            Log.e("Reading", "End");
            bw.close();
            br.close();
            socket.close();
        }catch(Exception e){
            e.printStackTrace();
            response = null;
        }
        return response;
    }
}
