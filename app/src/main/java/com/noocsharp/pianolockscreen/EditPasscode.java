package com.noocsharp.pianolockscreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by nihal on 7/27/2017.
 */

public class EditPasscode extends Activity {
    ArrayList<Integer> newPasscode;
    ArrayList<Integer> confirmPasscode;

    @BindView(R.id.edit_pascode_btn_ok) Button btnOk;
    @BindView(R.id.edit_passcode_btn_clear) Button btnClear;

    @BindView(R.id.edit_passcode_overlay_view) ImageView overlayView;

    @BindView(R.id.edit_passcode_instruction) TextView tv;

    boolean notePlayed = false;
    boolean confirmPass = false;

    TinyDB db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_passcode);
        ButterKnife.bind(this);

        newPasscode = new ArrayList<>();
        confirmPasscode = new ArrayList<>();
        init();

        db = new TinyDB(getApplicationContext());
    }

    @OnClick(R.id.edit_pascode_btn_ok)
    public void btnOkOnClick() {
        if (!confirmPass) {
            confirmPass = true;
            tv.setText(R.string.edit_passcode_confirm_text);
        } else {
            if (newPasscode.equals(confirmPasscode)) {
                db.putListInt("passcode", confirmPasscode);

                        /*
                        Intent openPreferences = new Intent(EditPasscode.this, PianoPreferencesActivity.class);
                        openPreferences.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(openPreferences, 0);
                        */
                finish();
            } else {
                newPasscode.clear();
                confirmPasscode.clear();
                confirmPass = false;
            }
        }
    }

    @OnClick(R.id.edit_passcode_btn_clear)
    public void btnClearOnClick() {
        newPasscode.clear();

    }

    @OnTouch(R.id.edit_passcode_imageview)
    public boolean pianoOnTouch(ImageView view, MotionEvent motionEvent) {
        view.performClick();
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        float w = view.getWidth();
        float h = view.getHeight();


        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            notePlayed = false;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            hideOverlay();
        }
        //Log.i(TAG, "Keys entered" + keysEntered);
        int keycode = keyForPos(w, h, x, y);
        if (!notePlayed) {
            if (!confirmPass) {
                newPasscode.add(keycode);
            } else {
                confirmPasscode.add(keycode);
            }

            drawOverlay(keycode);

            notePlayed = true;
        }

        return true;
    }

    private void init() {

        tv.setText(R.string.edit_passcode_instruction_text);


        View imageview = findViewById(R.id.edit_passcode_imageview);
        imageview.setBackgroundResource(R.drawable.ic_pianokeyboard);
    }

    private void hideOverlay() {
        overlayView.setVisibility(View.INVISIBLE);
    }

    private void drawOverlay(int keycode) {
        switch (keycode) {
            case 1:
                overlayView.setBackgroundResource(R.drawable.key1overlay);
                break;
            case 2:
                overlayView.setBackgroundResource(R.drawable.key2overlay);
                break;
            case 3:
                overlayView.setBackgroundResource(R.drawable.key3overlay);
                break;
            case 4:
                overlayView.setBackgroundResource(R.drawable.key4overlay);
                break;
            case 5:
                overlayView.setBackgroundResource(R.drawable.key5overlay);
                break;
            case 6:
                overlayView.setBackgroundResource(R.drawable.key6overlay);
                break;
            case 7:
                overlayView.setBackgroundResource(R.drawable.key7overlay);
                break;
            case 8:
                overlayView.setBackgroundResource(R.drawable.key8overlay);
                break;
            case 9:
                overlayView.setBackgroundResource(R.drawable.key9overlay);
                break;
            case 10:
                overlayView.setBackgroundResource(R.drawable.key10overlay);
                break;
            case 11:
                overlayView.setBackgroundResource(R.drawable.key11overlay);
                break;
            case 12:
                overlayView.setBackgroundResource(R.drawable.key12overlay);
                break;
        }
        overlayView.setVisibility(View.VISIBLE);
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
}
