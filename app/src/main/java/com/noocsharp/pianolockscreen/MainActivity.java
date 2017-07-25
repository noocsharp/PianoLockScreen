package com.noocsharp.pianolockscreen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Yaakov Shahak on 14/12/2016.
 */

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity.oncreate");
        super.onCreate(savedInstanceState);

        Intent i = new Intent(this, PianoPreferencesActivity.class);
        startActivity(i);
    }
}
