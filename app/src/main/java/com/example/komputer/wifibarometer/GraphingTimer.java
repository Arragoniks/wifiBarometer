package com.example.komputer.wifibarometer;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public abstract class GraphingTimer {
    //duration
    //interval
    //up/down (usually up) 1/0

    //units (usually ms)


    static final int MSG = 1;
    long duration;
    long interval;
    int direction;
    long mDuration;

    public GraphingTimer(long duration, long interval, int direction){
        this.duration = duration;
        this.interval = interval;
        this.direction = direction;
    }

    public void start(){
        mDuration = SystemClock.elapsedRealtime() + duration;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }

    public abstract void onTick(long millisGone);

    public abstract void onFinish();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            synchronized (GraphingTimer.this){

                long millisLeft = mDuration - SystemClock.elapsedRealtime();

                if(millisLeft <= 0){
                    onFinish();
                }else{
                    long tickStart = SystemClock.elapsedRealtime();
                    if(direction == 0){
                        onTick(duration - millisLeft);
                    }else {
                        onTick(millisLeft);
                    }
                    long tickDuration = SystemClock.elapsedRealtime() - tickStart;

                    long delay;
                    if(millisLeft < interval){
                        delay = millisLeft - tickDuration;

                        if(delay < 0){
                            delay = 0;
                        }
                    }else{
                        delay = interval - tickDuration;

                        while(delay < 0){
                            delay += interval;
                        }
                    }
                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };
}
