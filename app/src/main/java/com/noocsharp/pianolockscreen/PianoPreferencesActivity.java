package com.noocsharp.pianolockscreen;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Switch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


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
                        getPermissions();
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

    private boolean getPermissions() {

        int checkAlertPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW);
        int checkBootPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED);
        Log.i(TAG, "" + checkAlertPermission);
        Log.i(TAG, "" + checkBootPermission);

        if (checkAlertPermission == PackageManager.PERMISSION_GRANTED && checkBootPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (checkAlertPermission == PackageManager.PERMISSION_DENIED && checkBootPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                    3);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    56);

            return getPermissions();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

        }

    }

    public static class PianoPreferenceFragment extends PreferenceFragment
    {

        int PICK_IMAGE = 1;
        TinyDB db;
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Preference changeWallpaper = findPreference("change_wallpaper");

            changeWallpaper.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Wallpaper"), PICK_IMAGE);
                    return true;
                }
            });
        }

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            db = new TinyDB(getActivity().getApplicationContext());
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                if (checkPermission(getActivity())) {
                    Uri uri = data.getData();
                    db.putString("wallpaper_uri", uri.toString());
                }

                        /*
                int checkPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (checkPermission == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = data.getData();

                    try {
                        db.putString("wallpaper_uri", uri.toString());
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                        OutputStream fOut = null;

                        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pianolockscreen" + "/wallpaper.png";
                        Log.i(TAG, fullPath);

                        File file = new File(fullPath);
                        file.mkdirs();
                        file.createNewFile();
                        fOut = new FileOutputStream(file);

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();

                        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);

                }
                        */
            }
        }

        private boolean checkPermission(Activity activity) {
            int checkPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (checkPermission == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else if (checkPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                return checkPermission(activity);
            }

            return false;

        }
    }

}
