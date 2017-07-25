package com.noocsharp.pianolockscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

/**
 * Created by nihal on 7/24/2017.
 */

public class PianoPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = "PianoPreferencesActivit";
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PianoPreferenceFragment()).commit();

        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }

    public static class PianoPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (sharedPreferences.getBoolean("enableLockscreen", true)) {
            Log.i(TAG, "enableLockscreen");
            startService(new Intent(this, LockScreenService.class));
        } else if (sharedPreferences.getBoolean("enableLockscreen", false)) {
            Log.i(TAG, "disableLockscreen");
            stopService(new Intent(this, LockScreenService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        sp.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }
}
