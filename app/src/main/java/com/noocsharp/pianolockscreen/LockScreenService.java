package com.thedroidboy.lockscreendemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * Created by Yaakov Shahak on 14/12/2016.
 */

public class LockScreenService extends Service /*implements View.OnClickListener*/ {


    private LinearLayout linearLayout;
    //private RelativeLayout relativeLayout;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;
    private View.OnTouchListener pianoOnTouchListener;
    private View.OnClickListener buttonOnClickListener;
    private boolean verified = false;
    private ArrayList<Integer> keysEntered;
    private ArrayList<Integer> passcode;

    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundMap;

    private final int PIANO1  = 1;
    private final int PIANO2  = 2;
    private final int PIANO3  = 3;
    private final int PIANO4  = 4;
    private final int PIANO5  = 5;
    private final int PIANO6  = 6;
    private final int PIANO7  = 7;
    private final int PIANO8  = 8;
    private final int PIANO9  = 9;
    private final int PIANO10 = 10;
    private final int PIANO11 = 11;
    private final int PIANO12 = 12;

    private static final String TAG = "LockScreenService";

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, intentFilter);
        windowManager = ((WindowManager) getSystemService(WINDOW_SERVICE));
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN //draw on status bar
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,// hiding the home screen button
                PixelFormat.TRANSLUCENT);

        soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 100);
        soundMap = new HashMap<>();
        soundMap.put(PIANO1, soundPool.load(this));
    }

    private void init() {
        //linearLayout = new RelativeLayout(this);
        //windowManager.addView(linearLayout, layoutParams);
        //((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lock_screen, linearLayout);
        //View btnClose = linearLayout.findViewById(R.id.btn_close);
        //View imageView = linearLayout.findViewById(R.id.image_view);
        //imageView.setBackgroundResource(R.drawable.ic_pianokeyboard);
        //btnClose.setOnClickListener(this);

        linearLayout = new LinearLayout(this);
        windowManager.addView(linearLayout, layoutParams);
        ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lock_screen, linearLayout);

        keysEntered = new ArrayList<>();
        passcode = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

        pianoOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                float w = view.getWidth();
                float h = view.getHeight();


                //Log.i(TAG, "Keys entered" + keysEntered);
                keysEntered.add(keyForPos(w, h, x, y));

                if (keysEntered.equals(passcode)) {
                    verified = true;
                }

                return false;
            }
        };

        buttonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verified) {
                    windowManager.removeView(linearLayout);
                    linearLayout = null;
                    verified = false;
                    keysEntered = null;
                    passcode = null;
                } else {
                    keysEntered.clear();
                }
            }
        };

        View btnClose = linearLayout.findViewById(R.id.btn_ok);
        View imageView = linearLayout.findViewById(R.id.image_view);
        imageView.setOnTouchListener(pianoOnTouchListener);

        imageView.setBackgroundResource(R.drawable.ic_pianokeyboard);
        btnClose.setOnClickListener(buttonOnClickListener);
    }

    private int keyForPos(float width, float height, float x, float y) {
        float x_ratio = x/width;
        float y_ratio = y/height;
        if (y_ratio > .66) {
            if (x_ratio > 0 && x_ratio < .143) {
                return 1;
            } else if (x_ratio > .143 && x_ratio < .286) {
                return 3;
            } else if (x_ratio > .286 && x_ratio < .429) {
                return 5;
            } else if (x_ratio > .429 && x_ratio < .571) {
                return 6;
            } else if (x_ratio > .571 && x_ratio < .714) {
                return 8;
            } else if (x_ratio > .714 && x_ratio < .857) {
                return 10;
            } else if (x_ratio > .857 && x_ratio < 1.0) {
                return 12;
            }
        } else if (y_ratio < .66 && y_ratio > 0) {
            if (x_ratio > 0 && x_ratio < .090) {
                return 1;
            } else if (x_ratio > .090 && x_ratio < .170) {
                return 2;
            } else if (x_ratio > .170 && x_ratio < .259) {
                return 3;
            } else if (x_ratio > .259 && x_ratio < .340) {
                return 4;
            } else if (x_ratio > .340 && x_ratio < .426) {
                return 5;
            }  else if (x_ratio > .426 && x_ratio < .511) {
                return 6;
            } else if (x_ratio > .511 && x_ratio < .592) {
                return 7;
            } else if (x_ratio > .592 && x_ratio < .672) {
                return 8;
            } else if (x_ratio > .672 && x_ratio < .753) {
                return 9;
            } else if (x_ratio > .753 && x_ratio < .834) {
                return 10;
            } else if (x_ratio > .834 && x_ratio < .902) {
                return 11;
            } else if (x_ratio > .902 && x_ratio < 1.0) {
                return 12;
            }
        }
        return 0;
    }

    /*
    @Override
    public void onClick(View view) {
        //windowManager.removeView(linearLayout);
        //linearLayout = null;
        windowManager.removeView(relativeLayout);
        relativeLayout = null;
    }
    */

    @Override
    public void onDestroy() {
        unregisterReceiver(screenReceiver);
        super.onDestroy();
    }

    BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           /*if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && linearLayout == null) {
                init();
            }*/
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && linearLayout == null) {
                init();
            }
        }
    };


}
