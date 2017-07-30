package com.noocsharp.pianolockscreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Switch;


/**
 * Created by nihal on 7/24/2017.
 */

public class PianoPreferencesActivity extends PreferenceActivity{

    private static final String TAG = "PianoPreferencesActivit";
    //private SharedPreferences sp;
    private TinyDB db;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "PianoPreferencesActivity.oncreate");
        /*
        setContentView(R.layout.preferences_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.preferences_toolbar);
        toolbar.setTitle(R.string.edit_passcode_toolbar_title);
        toolbar.setTitleTextColor(Color.WHITE);
        */

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PianoPreferenceFragment()).commit();

        //sp = PreferenceManager.getDefaultSharedPreferences(this);
        db = new TinyDB(getApplicationContext());


        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Log.i(TAG, String.format("Preference changed: %s", s));
                if (s.equals("enableLockScreen")) {
                    Log.i(TAG, "enableLockScreen changed");
                    if (db.getBoolean(s)) {
                        startService(new Intent(PianoPreferencesActivity.this, LockScreenService.class));
                    } else {
                        stopService(new Intent(PianoPreferencesActivity.this, LockScreenService.class));
                    }
                }
            }
        };

        db.registerOnSharedPreferenceChangeListener(listener);
        //sp.registerOnSharedPreferenceChangeListener(listener);
        TypedValue tv = new TypedValue();


        /*
        int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int topMargin = toolbar.getHeight();
        getListView().setPadding(horizontalMargin, topMargin, horizontalMargin, verticalMargin);
        */
    }

    public static class PianoPreferenceFragment extends PreferenceFragment
    {

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
