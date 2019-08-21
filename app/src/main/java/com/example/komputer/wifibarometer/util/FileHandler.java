package com.example.komputer.wifibarometer.util;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class FileHandler{

//    private static final int WRITE_MODE_APPEND = 0;
//    private static final int WRITE_MODE_REWRITE = 1;
    private String settingsFile = "/storage/emulated/0/Android/data/com.example.komputer.wifibarometer/files/settings";
//    private String pathSaveBufferFolder;
    private String bufferFile = "/storage/emulated/0/Android/data/com.example.komputer.wifibarometer/files/bufferFile";
//    private String bufferFolder;
//    private String appFolder = "/storage/emulated/0/Android/data/com.example.komputer.wifibarometer/files/";
    private String[] emptyArray = new String[0];

    private static String extension = "";

    private class ReadingTask extends AsyncTask<String, Void, List<String>>{
        @Override
        protected List<String> doInBackground(String... strings) {
            List<String> arrayList = new ArrayList<>();
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(strings[0]))) {
                arrayList.add(bufferedReader.readLine());
                while(arrayList.get(arrayList.size()-1) != null){
                    arrayList.add(bufferedReader.readLine());
                }
                arrayList.remove(arrayList.size()-1);

            }catch(Exception e){
                e.printStackTrace();
            }
            return arrayList;
        }
    }

    private class AppendingTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(strings[0], true))){
                for(int i = 1; i < strings.length; i++){
                    bufferedWriter.append(strings[i]);
                    bufferedWriter.newLine();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    private class RewritingTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(strings[0]))){
                for(int i = 1; i < strings.length; i++){
                    bufferedWriter.write(strings[i]);
                    bufferedWriter.newLine();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    private class CopyingTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {

            try (InputStream in = new FileInputStream(strings[0])) {
                try (OutputStream out = new FileOutputStream(strings[1])) {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

    }

    public static void setExtension(String extension1){
        extension = extension1;
    }

//    public FileHandler(){
//        bufferFile = appFolder + bufferFile;
//        propertiesFile = appFolder + propertiesFile;
//        emptyArray = new String[0];
//    }

    public static boolean checkTheFile(String path){
        return new File(path).isFile();
    }

/*
    FileHandler(){

        File file1 = new File(bufferFile);
        File file2 = new File(propertiesFile);
        checkExistance(file1);
        checkExistance(file2);
    }

    private void checkExistance(File file){
        if(!file.exists()){
            try {
                file.createNewFile();
            }catch (Exception e){
                Log.e("Exception", e.toString());
            }
        }
    }
*/

    public void clearBuffer(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try{
                   FileWriter fileWriter = new FileWriter(bufferFile);
                   fileWriter.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
//        RewritingTask rewritingTask = new RewritingTask();
//        rewritingTask.execute(bufferFile, "");
    }

    public void writeBuffer(List<String> list){
//        ArrayList<String> newList = new ArrayList<>(list);
//        newList.add(0, bufferFile);
        String[] array = new String[list.size()+1];
        array[0] = bufferFile;
        for(int i = 1; i < array.length; i++){
            array[i] = list.get(i-1);
        }
        AppendingTask appendingTask = new AppendingTask();
        appendingTask.execute(array);
    }

    public void editSettings(Map<String, String> newSettings){
        List<String> newList = new ArrayList<>();
        newList.add(settingsFile);
        for(Map.Entry<String, String> pair : newSettings.entrySet()){
            Log.e("Settings", pair.toString());
            newList.add(pair.toString());
        }
        RewritingTask rewritingTask = new RewritingTask();
        rewritingTask.execute(newList.toArray(emptyArray));
    }

    public Map<String, String> readSettings(){
        ReadingTask readingTask = new ReadingTask();
        readingTask.execute(settingsFile);
        List<String> list = null;
        try {
             list = readingTask.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> settings = new HashMap<>();
        String temp;
        Log.e("List", list.toString());
        for(int i = 0, index; i < list.size(); i++){
            temp = list.get(i);
            index = temp.indexOf("=");
            Log.e("Reading key", temp.substring(0, index));
            Log.e("Reading val", temp.substring(index+1, temp.length()));
            settings.put(temp.substring(0, index), temp.substring(index+1, temp.length()));
        }

        return settings;
    }

    public static String generateDefaultName(){
//        Date time = new Date();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
//        Log.e("Hour", Integer.toString(calendar.get(Calendar.HOUR)));
//        Log.e("Hour of day", Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
        return "filePoints_"+calendar.get(Calendar.SECOND)+"_"
                +calendar.get(Calendar.MINUTE)+"_"
                +calendar.get(Calendar.HOUR_OF_DAY)+"_"
                +calendar.get(Calendar.DAY_OF_MONTH)+"_"
                +(calendar.get(Calendar.MONTH) + 1)+"_"
                +calendar.get(Calendar.YEAR);
    }

    public void saveBufferInFile(String filePath){
        File outFile = new File(filePath);
        try {
            outFile.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(outFile.exists()){
            CopyingTask copyingTask = new CopyingTask();
            copyingTask.execute(bufferFile, filePath);
        }
    }

    public List<String> getGraphPointFromFile(String path) throws Exception{
        ReadingTask readingTask = new ReadingTask();
        readingTask.execute(path);
        return readingTask.get();
    }
}
