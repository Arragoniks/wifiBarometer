package com.example.komputer.wifibarometer;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {//path fileName request context

    public void writeBuffer(String data){//without context file methods
        Writer output;
        try {
            output = new BufferedWriter(new FileWriter("/storage/sdcard0/Android/data/com.example.komputer.wifibarometer/files/bufferfile.txt", true));  //clears file every time
            output.append(data + "\n");
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearbuffer(Context context){
        File pathFile = context.getExternalFilesDir(null);
        File file = new File(pathFile, "bufferfile.txt");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write("".getBytes());
            stream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void editProperties(String key, String value, Context context){
        File pathFile = context.getExternalFilesDir(null);
        File file = new File(pathFile, "properties.txt");
        int length = (int) file.length();

        byte[] bytes = new byte[length];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            in.read(bytes);
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        String buffer = new String(bytes);

        buffer = buffer.replaceAll(key+"=\\d*", key+"="+value);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(buffer.getBytes());
            stream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String readProperties(String key, Context context){
        String value = "";

        //make read method

        File pathFile = context.getExternalFilesDir(null);
        File file = new File(pathFile, "properties.txt");
        int length = (int) file.length();

        byte[] bytes = new byte[length];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            in.read(bytes);
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        String buffer = new String(bytes);

        //find by the key needed value
        String[] srcarray = buffer.split("D");
        for(String temp : srcarray){
            if(temp.contains(key)){
                value = temp.split("=")[1];
            }
        }
        return value;
    }

    public void saveBufferInFile(Context context, String path, String name){
        File pathFile = context.getExternalFilesDir(null);
        File sourceFile = new File(pathFile, "bufferfile.txt");
        Time time = new Time();
        time.setToNow();
        File file1 = new File(path);
        String FOLDER_TO_SAVE = file1.getAbsolutePath();
        File destFile;
        if(name == null) {
            destFile = new File(FOLDER_TO_SAVE, "filePoints"
                    + Integer.toString(time.year)
                    + Integer.toString(time.month)
                    + Integer.toString(time.hour)
                    + Integer.toString(time.minute)
                    + Integer.toString(time.second) + ".txt");
        }else{
            if(!name.endsWith(".txt")){
                name += ".txt";
            }
            destFile = new File(FOLDER_TO_SAVE, name);
        }
        try{
            if(!destFile.exists()) {
                destFile.createNewFile();
            }

            FileChannel source = null;
            FileChannel destination = null;

            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            }
            finally {
                if(source != null) {
                    source.close();
                }
                if(destination != null) {
                    destination.close();
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<String> getGraphPoint(String filePath){
        List<String> points = new ArrayList<>();
        FileInputStream is;
        BufferedReader reader;
        final File file = new File(filePath);
        try {
            is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while (line != null) {
                points.add(line);
                line = reader.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return points;
    }
}
