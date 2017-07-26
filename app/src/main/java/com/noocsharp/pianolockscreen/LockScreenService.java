package com.noocsharp.pianolockscreen;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

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
    private boolean verified = false;
    private ArrayList<Integer> keysEntered;
    private ArrayList<Integer> passcode;

    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundMap;

    private SharedPreferences sp;

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
        Log.i(TAG, "LockScreenService.onCreate");
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
        soundMap.put(PIANO1,  soundPool.load(this, R.raw.c4, 1));
        soundMap.put(PIANO2,  soundPool.load(this, R.raw.db4, 1));
        soundMap.put(PIANO3,  soundPool.load(this, R.raw.d4, 1));
        soundMap.put(PIANO4,  soundPool.load(this, R.raw.eb4, 1));
        soundMap.put(PIANO5,  soundPool.load(this, R.raw.e4, 1));
        soundMap.put(PIANO6,  soundPool.load(this, R.raw.f4, 1));
        soundMap.put(PIANO7,  soundPool.load(this, R.raw.gb4, 1));
        soundMap.put(PIANO8,  soundPool.load(this, R.raw.g4, 1));
        soundMap.put(PIANO9,  soundPool.load(this, R.raw.ab4, 1));
        soundMap.put(PIANO10, soundPool.load(this, R.raw.a4, 1));
        soundMap.put(PIANO11, soundPool.load(this, R.raw.bb4, 1));
        soundMap.put(PIANO12, soundPool.load(this, R.raw.b4, 1));

        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }

    private void playSound(int sound, float fSpeed) {
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;


        soundPool.play(soundMap.get(sound), volume, volume, 1, 0, fSpeed);
    }

    private void playSoundFromKeycode(int keycode) {
        switch (keycode) {
            case 1:
                playSound(PIANO1, 1.0f);
                break;
            case 2:
                playSound(PIANO2, 1.0f);
                break;
            case 3:
                playSound(PIANO3, 1.0f);
                break;
            case 4:
                playSound(PIANO4, 1.0f);
                break;
            case 5:
                playSound(PIANO5, 1.0f);
                break;
            case 6:
                playSound(PIANO6, 1.0f);
                break;
            case 7:
                playSound(PIANO7, 1.0f);
                break;
            case 8:
                playSound(PIANO8, 1.0f);
                break;
            case 9:
                playSound(PIANO9, 1.0f);
                break;
            case 10:
                playSound(PIANO10, 1.0f);
                break;
            case 11:
                playSound(PIANO11, 1.0f);
                break;
            case 12:
                playSound(PIANO12, 1.0f);
                break;
        }
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

        View.OnTouchListener pianoOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                float w = view.getWidth();
                float h = view.getHeight();


                //Log.i(TAG, "Keys entered" + keysEntered);
                int keycode = keyForPos(w, h, x, y);
                keysEntered.add(keycode);

                if (sp.getBoolean("playNote", true)) {
                    playSoundFromKeycode(keycode);
                }

                if (keysEntered.size() == passcode.size()) {

                    if (keysEntered.equals(passcode)) {
                        unlock();
                    } else {
                        keysEntered.clear();
                    }
                }

                return false;
            }
        };

        View.OnClickListener buttonOkOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unlock();
            }
        };
        View.OnClickListener buttonClearOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keysEntered.clear();
            }
        };

        View btnOk = linearLayout.findViewById(R.id.btn_ok);
        View btnClear = linearLayout.findViewById(R.id.btn_clear);
        View imageView = linearLayout.findViewById(R.id.image_view);
        imageView.setOnTouchListener(pianoOnTouchListener);

        imageView.setBackgroundResource(R.drawable.ic_pianokeyboard);
        btnOk.setOnClickListener(buttonOkOnClickListener);
        btnClear.setOnClickListener(buttonClearOnClickListener);

    }

    private void unlock() {
        windowManager.removeView(linearLayout);
        linearLayout = null;
        verified = false;
        keysEntered = null;
        passcode = null;
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
        if (soundPool != null) {
            soundPool.release();
        }
        soundMap = null;
        Log.i(TAG, "LockScreenService.onDestroy");
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
