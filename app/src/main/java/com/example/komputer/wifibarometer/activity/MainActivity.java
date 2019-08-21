package com.example.komputer.wifibarometer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.komputer.wifibarometer.R;
import com.example.komputer.wifibarometer.fragment.Communicator;
import com.example.komputer.wifibarometer.fragment.FragmentGraphing;

public class MainActivity extends AppCompatActivity implements Communicator {

//    public static final int FRAGMENT_MAIN_ID = 0;
//    public static final int FRAGMENT_PROPERTIES_ID = 1;
//    public static final int FRAGMENT_SENSOR_PROPERTIES_ID = 2;
    public static final int FRAGMENT_GRAPHING_ID = 3;
//    public static final int FRAGMENT_SETTINGS_ID = 4;

    public static final String APP_FILE_EXTENSION = ".wbdf";

    private int prevCallFragment;

    private static ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
//        initLayout();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentGraphing fragmentGraphing = new FragmentGraphing();
        ft.add(R.id.container, fragmentGraphing, "fragmentGraphing");
        ft.commit();

    }

    public static void setTileForBar(String text){
        actionBar.setTitle(text);
    }

    private void initLayout(){
        changeFragment(0, MainActivity.FRAGMENT_GRAPHING_ID);
    }

    @Override
    public void changeFragment(int previous, int next) {
        if(next == -1)
            next = prevCallFragment;
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;
        FragmentTransaction ft = fm.beginTransaction();
        switch(next){
            case FRAGMENT_GRAPHING_ID:
                fragment = new FragmentGraphing();
                break;
        }
        ft.replace(R.id.container, fragment, "fragment");
        ft.addToBackStack(null);
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.commit();
        prevCallFragment = previous;
    }
}