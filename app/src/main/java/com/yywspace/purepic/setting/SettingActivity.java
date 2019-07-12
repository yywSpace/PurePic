package com.yywspace.purepic.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.yywspace.purepic.R;

import java.util.Map;


public class SettingActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private static String TAG = SettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView state = findViewById(R.id.model_state);
        if (isModuleActive())
            state.setText(R.string.model_active);
        else
            state.setText(R.string.model_disactive);
        getFragmentManager().beginTransaction().replace(R.id.preference_content, new SettingFragment()).commit();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }


    private static boolean isModuleActive() {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: " + prefs.getAll().toString());
        Setting.writeSetting(prefs);
        Map<String, Boolean> map = Setting.getSetting();
        for (String str :
                map.keySet()) {
            Log.d(TAG, "onPause: " + map.get(str));
        }
    }
}

