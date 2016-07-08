package com.example.liangweiwu.downloadmanager.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * Created by xinxin.li on 16/7/8.
 */
public class AccessibilityServiceUtils {
    private AccessibilityServiceUtils() {
    }

    public static boolean checkAccessibilitySettingState(Context context, String service){
        int accessibilityEnabled = 0;
        try{
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        }catch (Exception e){
            e.printStackTrace();
        }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');

        if(accessibilityEnabled == 1){
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if(settingValue != null){
                colonSplitter.setString(settingValue);
                while(colonSplitter.hasNext()){
                    String accessibilityService = colonSplitter.next();
                    if(accessibilityService.equalsIgnoreCase(service)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
