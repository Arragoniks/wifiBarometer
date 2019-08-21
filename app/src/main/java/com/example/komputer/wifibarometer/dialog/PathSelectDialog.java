package com.example.komputer.wifibarometer.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.komputer.wifibarometer.util.FileHandler;
import com.example.komputer.wifibarometer.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class PathSelectDialog extends AlertDialog.Builder
        implements AdapterView.OnItemClickListener{

    private String currentPath;
    private List<File> files = new ArrayList<>();
    private LinearLayout linearLayout;
    private ListView listView;
    private EditText editText;
    private TextView title;
    private int mode;
    private int selectedItem = -1;
    private Button buttonOk;
    private Button buttonCancel;
    private AlertDialog alertDialog;
    private String searchPrefix = "";
    private String prevPath;
    private String extension = "";

    private AlertDialog initDialog(final AlertDialog alertDialog){
        this.alertDialog = alertDialog;
        this.buttonOk = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        this.buttonCancel = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file;
                if(selectedItem < 0){
                    file = new File(currentPath);
                }else{
                    file = (File) listView.getItemAtPosition(selectedItem);
                }

                if(mode == 0) {
                    if(file.isFile()){
                        listener.onSelectedFile(file.getPath());
                    }else{
                        Toast.makeText(getContext(), "Incorrect file", Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                }else{
                    String fileName = editText.getText().toString() + extension;
                    String titleText = "";
                    final String filePath = currentPath + "/" + fileName;
                    for(int i = 0; i < files.size(); i++){
                        if(files.get(i).getName().equals(fileName)){
                            titleText = "Replace this file?";
                            break;
                        }
                    }
                    if(titleText.equals("")){
                        titleText = "Save this file?";
                    }
                    ConfirmDialog confirmDialog = new ConfirmDialog(getContext(), titleText, fileName);
                    confirmDialog.setConfirmDialogListener(new ConfirmDialog.ConfirmDialogListener() {
                        @Override
                        public void onSelected(boolean button) {
                            if(button){
                                listener.onSelectedFile(filePath);
                                alertDialog.dismiss();
                            }
                        }
                    });
                    confirmDialog.show();
                }
            }
        });
        if(mode == 0){
            buttonOk.setTextColor(0xff707070);
            buttonOk.setClickable(false);
        }else{
            editText.setText(FileHandler.generateDefaultName());
        }
        return alertDialog;
    }

    private FilenameFilter filenameFilter;
    public PathSelectDialog setFilter(final String filter) {
        filenameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File file, String fileName) {
                File tempFile = new File(String.format("%s/%s", file.getPath(), fileName));
                fileName = fileName.toLowerCase();
                if(fileName.toLowerCase().contains(searchPrefix)){
                    if (tempFile.isFile())
                        return tempFile.getName().matches(filter);
                    return true;
                }else{
                    return false;
                }
            }
        };
        return this;
    }

    public interface PathSelectListener{
        void onSelectedFile(String filePath);
    }
    private PathSelectListener listener;

    public PathSelectDialog setOpenDialogListener(PathSelectListener listener) {
        this.listener = listener;
        return this;
    }

    private class FileAdapter extends ArrayAdapter<File> {
        public FileAdapter(Context context, List<File> files){
            super(context, R.layout.file_adapter_view, R.id.textView, files);
        }

        private String[] splitIgnoreCase(String text, String regex){
            char[] textChar = text.toCharArray();
            String[] resultArray = new String[textChar.length];
            String temp = "";
            int k = 0;
            for(int i = 0; i < textChar.length; i++){
                temp += textChar[i];
                if(temp.toLowerCase().endsWith(regex)){
                    if(temp.length() > regex.length()){
                        resultArray[k++] = temp.substring(0, temp.length() - regex.length());
                    }
                    resultArray[k++] = "<font color=" + "#ed26ab" + ">" +
                            temp.substring(temp.length() - regex.length(), temp.length()) +
                            "</font>";
                    temp = "";
                }else if(i+1 >= textChar.length){
                    resultArray[k++] = temp;
                }
            }
            String[] splitArray = new String[k];
            for(int i = 0; i < k; i++){
                splitArray[i] = resultArray[i];
            }
            return splitArray;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            File file = getItem(position);
            if(mode == 0){
                String fileName = file.getName();
                if(selectedItem == position){
                    view.setBackgroundColor(0xff4d90fe);
                }else if(fileName.toLowerCase().contains(searchPrefix) && !searchPrefix.equals("")){
//                    Log.e("Splitting", Arrays.toString(splitIgnoreCase(fileName, searchPrefix)));
                    String[] tempArray = splitIgnoreCase(fileName, searchPrefix);
                    String fileNameTemp = "";
                    for(int i = 0; i < tempArray.length; i++){
                        fileNameTemp += tempArray[i];
                    }
                    fileName = fileNameTemp;
                    view.setBackgroundColor(0xffffffff);
                }else{
                    view.setBackgroundColor(0xffffffff);
                }
                view.setText(Html.fromHtml(fileName));
            }else{
                view.setText(file.getName());
                if(selectedItem == position){
                    view.setBackgroundColor(0xff4d90fe);
                }else{
                    view.setBackgroundColor(0xffffffff);
                }
            }
            return view;
        }
    }

    public PathSelectDialog(Context context, int mode, String extension){
        super(context);
        this.extension = extension;
        setFilter(".*\\" + extension);

        this.mode = mode;
        currentPath = Environment.getExternalStorageDirectory().getPath();
        prevPath = Environment.getExternalStorageDirectory().getPath();
//        if(mode == 1){
//            fileName = FileHandler.generateDefaultName();
//        }

        linearLayout = createLinearLayout(context);
        listView = createListView(context);
        editText = createEditText(context);

        title = createTitle(context);

        linearLayout.addView(title);
        linearLayout.addView(createBackItem(context));
        linearLayout.addView(editText);
        linearLayout.addView(listView);

        changeTitle();

        TextView titleText = createTextView(context, android.R.style.TextAppearance_DeviceDefault_DialogWindowTitle);
        if(mode == 0){
            titleText.setText("Open File");
        }else{
            titleText.setText("Save File");
        }

        setCustomTitle(titleText)
//        setTitle(titleText)
                .setView(linearLayout)
                .setPositiveButton(R.string.ok, null)
//                .setNegativeButton(R.string.no, null)
                .setNeutralButton(R.string.cancel, null);
    }

    @Override
    public AlertDialog show() {
        files.addAll(getFiles(currentPath));
        listView.setAdapter(new FileAdapter(getContext(), files));
//        alertDialog = ;
//        setButtonOk(alertDialog.getButton(AlertDialog.BUTTON_POSITIVE));
//        setButtonCancel(alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
        return initDialog(super.show());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayAdapter<File> adapter = (FileAdapter) parent.getAdapter();
        File file = adapter.getItem(position);
        if(file.isDirectory()){
            currentPath = file.getPath();
            rebuildFiles(adapter);
        }else{
            if(selectedItem != position){
                selectedItem = position;
                if(mode == 0){
                    buttonOk.setTextColor(0xffff4081);
                    buttonOk.setClickable(true);
                }else{
                    String fileName = file.getName();
                    editText.setText(fileName.substring(0, fileName.lastIndexOf(extension)));
                }
            }else{
                selectedItem = -1;
                if(mode == 0){
                    buttonOk.setTextColor(0xff707070);
                    buttonOk.setClickable(false);
                }else{
                    editText.setText("");
                }
            }
            adapter.notifyDataSetChanged();

        }
    }

    private EditText createEditText(Context context){
        final EditText editText = new EditText(context);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("Char Length", Integer.toString(s.length()));
                if(mode == 0){
                    searchPrefix = s.toString().toLowerCase();
                    rebuildFiles((FileAdapter) listView.getAdapter());
                }else{
                    if(s.length() > 0){
                        buttonOk.setTextColor(0xffff4081);
                        buttonOk.setClickable(true);
                    }else{
                        buttonOk.setTextColor(0xff707070);
                        buttonOk.setClickable(false);
                        editText.setError("Type file Name");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return editText;
    }

    private ListView createListView(Context context){
        final ListView listView = new ListView(context);
        listView.setOnItemClickListener(this);
        return listView;
    }

    private Display getDisplay(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display;
    }

    private Point getScreenSize(){
        Point screenSize = new Point();
        getDisplay().getSize(screenSize);
        return screenSize;
    }

    private TextView createTextView(Context context, int style) {
        TextView textView = new TextView(context);
        textView.setTextAppearance(context, style);
//        int itemHeight = getItemHeight(context);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
//        textView.setMinHeight(itemHeight);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(15, 0, 0, 0);
        textView.setTextSize(30);
        return textView;
    }

    private TextView createBackItem(Context context) {
        TextView textView = createTextView(context, android.R.style.TextAppearance_DeviceDefault_DialogWindowTitle);
        textView.setText("...");
//        Drawable drawable = getContext().getResources().getDrawable(android.R.drawable.ic_menu_directions);
//        drawable.setBounds(0, 0, 60, 60);
//        textView.setCompoundDrawables(drawable, null, null, null);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                File file = new File(currentPath);
                File parentDirectory = file.getParentFile();
                if (parentDirectory != null) {
                    prevPath = currentPath;
                    currentPath = parentDirectory.getPath();
//                    Log.e("Path", currentPath);
                    rebuildFiles(((FileAdapter) listView.getAdapter()));
                }
            }
        });
        return textView;
    }

    public int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.left + bounds.width() + 80;
    }

    private void changeTitle() {
        String titleText = currentPath;
        int screenWidth = getScreenSize().x;
        int maxWidth = (int) (screenWidth * 0.99);
        if (getTextWidth(titleText, title.getPaint()) > maxWidth) {
            while (getTextWidth("..." + titleText, title.getPaint()) > maxWidth)
            {
                int start = titleText.indexOf("/", 2);
                if (start > 0)
                    titleText = titleText.substring(start);
                else
                    titleText = titleText.substring(2);
            }
            title.setText("..." + titleText);
        } else {
            title.setText(titleText);
        }
    }

    private TextView createTitle(Context context){
        TextView textView = createTextView(context,
                android.R.style.TextAppearance_DeviceDefault_DialogWindowTitle);
        return textView;
    }


    private LinearLayout createLinearLayout(Context context){
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumHeight(getScreenSize().y);
        return linearLayout;
    }

    private void rebuildFiles(ArrayAdapter<File> adapter){
        try {
            List<File> fileList = getFiles(currentPath);
            files.clear();
            files.addAll(fileList);
            adapter.notifyDataSetChanged();
            changeTitle();
            selectedItem = -1;
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Access denied", Toast.LENGTH_SHORT).show();
            currentPath = prevPath;
        }
    }

    private List<File> getFiles(String directoryPath){
        File directory = new File(directoryPath);
        List<File> fileList = Arrays.asList(directory.listFiles(filenameFilter));
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(o1.isDirectory() && o2.isFile()){
                   return -1;
                }else if(o1.isFile() && o2.isDirectory()){
                    return 1;
                }else{
                    return o1.getPath().compareTo(o2.getPath());
                }
            }
        });
        return fileList;
    }
}
