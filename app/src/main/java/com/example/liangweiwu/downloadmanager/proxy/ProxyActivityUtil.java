package com.example.liangweiwu.downloadmanager.proxy;

import android.content.Context;
import android.content.Intent;

/**
 * Created by xinxin.li on 16/7/13.
 */
public class ProxyActivityUtil {
    private static final String defaultPath = "/mnt/sdcard/ProxyHost/client.apk";
    public static void loadProxy(Context context){
        loadProxy(context,defaultPath);
    }

    public static void loadProxy(Context context,String apkPath){
        Intent intent = new Intent(context,ProxyActivity.class);
        intent.putExtra(ProxyActivity.EXTRA_DEX_PATH,apkPath);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
