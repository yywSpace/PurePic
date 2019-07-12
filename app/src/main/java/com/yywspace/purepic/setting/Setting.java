package com.yywspace.purepic.setting;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Setting {
    public final static String SETTING_FILE_PATH = "/sdcard";//"/storage/emulated/0/Android/data/com.example.purepicacg_xposed/files"; //"/data/user/0/com.example.purepicacg_xposed/files/";

    public static void writeSetting(SharedPreferences prefs) {
        String settingFilePath = SETTING_FILE_PATH + "/purepic_xposed_setting.txt";
        Log.d("SettingActivity", "writeSetting: " + settingFilePath);
        File settingFile = new File(settingFilePath);
        settingFile.setReadable(true);
        settingFile.setWritable(true);
        if (settingFile.exists())
            settingFile.delete();
        try {
            settingFile.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(settingFile));
            Map<String, Boolean> setting = (Map<String, Boolean>) prefs.getAll();
            for (String settingName : setting.keySet()) {
                // Log.d("SettingActivity", "writeSetting: " + settingName + ":" + prefs.getBoolean(settingName, false));
                bw.write(settingName + ":" + prefs.getBoolean(settingName, false) + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, Boolean> getSetting() {
        String settingFilePath = SETTING_FILE_PATH + "/purepic_xposed_setting.txt";
        File settingFile = new File(settingFilePath);
        Map<String, Boolean> setting = new HashMap<>();
        if (!settingFile.exists())
            return null;
        String singleSetting = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(settingFile));
            while ((singleSetting = br.readLine()) != null) {
                String[] kv = singleSetting.split(":");
                setting.put(kv[0], Boolean.valueOf(kv[1]));
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return setting;
    }
}
