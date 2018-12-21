package com.example.komputer.wifibarometer;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class DownloadTask extends AsyncTask<String, Void, Integer> {

    @Override
    protected Integer doInBackground(String... strings) {
        String htmlPage = "";
        Integer pressureInt;
        try {
            URL pageURL = new URL(strings[0]);
            URLConnection uc = pageURL.openConnection();
            BufferedReader buff = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            StringBuilder buf=new StringBuilder();
            String line=null;
            while ((line=buff.readLine()) != null) {
                buf.append(line + "\n");
            }
            htmlPage = buf.toString();
        }catch(Exception e){
            e.printStackTrace();
        }

//parsing
        int beginIndex = htmlPage.indexOf("Pr") + 2;
        int lastIndex = htmlPage.indexOf("Pr", beginIndex+1);
        pressureInt = Integer.parseInt(htmlPage.substring(beginIndex, lastIndex));
        //Log.e("Msg", pressureInt.toString());

        return pressureInt;
    }

}
