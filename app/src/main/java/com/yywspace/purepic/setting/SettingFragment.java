package com.yywspace.purepic.setting;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.yywspace.purepic.R;

public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
    }
}
