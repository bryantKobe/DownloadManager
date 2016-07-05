package com.example.liangweiwu.downloadmanager.Helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import com.example.liangweiwu.downloadmanager.Model.GameInformation;


public class ApkInfoAccessor {
    String mFilePath;
    Context mContext;
    GameInformation mInfo;

    public ApkInfoAccessor(String filePath, Context context) {
        this(filePath, context,null);
    }

    public ApkInfoAccessor(String filePath, Context context, GameInformation info) {
        this.mFilePath = filePath;
        this.mContext = context;
        this.mInfo = info;
        if(this.mInfo == null){
            this.mInfo = new GameInformation();
        }
    }

    public GameInformation drawPacks(){

        if(mInfo.getAttribution("package") != null){
            return mInfo;
        }

        PackageManager pm = mContext.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(mFilePath,PackageManager.GET_ACTIVITIES);

        if(mInfo != null){
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            Drawable icon = pm.getApplicationIcon(appInfo);
            int minSdkVersion = appInfo.minSdkVersion;
            int targetSdkVersion = appInfo.targetSdkVersion;
            int versionCode = packageInfo.versionCode;
            String permissions = appInfo.permission;
            String versionName = packageInfo.versionName;
            String packageName = packageInfo.packageName;
            String appName = pm.getApplicationLabel(appInfo).toString();

            mInfo.setAttribute("name",appName);
            mInfo.setAttribute("package",packageName);
            mInfo.setAttribute("versionCode",versionCode);
            mInfo.setAttribute("versionName",versionName);
            mInfo.setAttribute("permissions",permissions);
            mInfo.setAttribute("minSdkVersion",minSdkVersion);
            mInfo.setAttribute("targetSdkVersion",targetSdkVersion);
            mInfo.setAttribute("icon",icon);
        }

        return mInfo;
    }
}
