package com.example.liangweiwu.downloadmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.liangweiwu.downloadmanager.activitys.MainActivity;
import com.example.liangweiwu.downloadmanager.utils.DownloadTaskPool;


public class ApkInstallationListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        String packageName = intent.getData().getSchemeSpecificPart();
        String appName = DownloadTaskPool.getInstance().setApkInstalled(packageName);
        Toast.makeText(context,appName + " 已经安装!",Toast.LENGTH_LONG).show();
    }
}
