package com.example.komputer.wifibarometer;

import android.content.Context;
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
import java.io.Writer;

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

    /*public String readFile(String path, String fileName, Context context){//try with bufferedReader for big files
        String buffer = "";
        File pathFile = context.getExternalFilesDir(null);
        File file = new File(pathFile, fileName);
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

        buffer = new String(bytes);
        Log.e("Buffer", buffer);
        return buffer;
    }*/

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
        Log.e("Buffer", buffer);

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
        Log.e("Buffer", buffer);

        //find by the key needed value
        String[] srcarray = buffer.split("D");
        for(String temp : srcarray){
            if(temp.contains(key)){
                value = temp.split("=")[1];
            }
        }
        return value;
    }
}
