package com.example.liangweiwu.downloadmanager.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.example.liangweiwu.downloadmanager.Model.GameInformation;
import com.example.liangweiwu.downloadmanager.Utils.FileUtils;
import com.example.liangweiwu.downloadmanager.Utils.GameInformationUtils;

import java.io.File;
import java.util.HashMap;


public class ApkInfoAccessor {
    private static ApkInfoAccessor mAccessor;
    private Context mContext;
    private PackageManager packageManager;

    private ApkInfoAccessor(Context context){
        this.mContext = context;
        this.packageManager = context.getPackageManager();
    }
    public static void init(Context context){
        if(mAccessor == null){
            mAccessor = new ApkInfoAccessor(context.getApplicationContext());
        }
    }
    public static ApkInfoAccessor getInstance(){
        return mAccessor;
    }
    /*
    public ApkInfoAccessor(String filePath, Context context) {
        this(filePath, context,null);
    }

    public ApkInfoAccessor(String filePath, Context context, GameInformation info) {
        this.mFilePath = filePath;
        this.mContext = context;
        this.mInfo = info;
        if(this.mInfo == null){
            this.mInfo = GameInformationUtils.getInstance().createGameInfo();
        }
        if(mPackItems == null){
            mPackItems = new HashMap<>();
        }
    }
    */
    /**
     **  @param fileName: 文件名字
     **  @param  mInfo: 更新的对象,可为null
    ***/
    public GameInformation drawPackages(String fileName,GameInformation mInfo){
        String mFilePath = FileUtils.DIR_PACKAGE + fileName;
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(mFilePath,PackageManager.GET_ACTIVITIES);
        if(packageInfo == null){
            return null;
        }
        if(mInfo == null){
            mInfo = GameInformationUtils.getInstance().createGameInfo("local");
        }

        ApplicationInfo appInfo = packageInfo.applicationInfo;
        appInfo.publicSourceDir = mFilePath;
        appInfo.sourceDir = mFilePath;
        Drawable icon = packageManager.getApplicationIcon(appInfo);
        int targetSdkVersion = appInfo.targetSdkVersion;
        int versionCode = packageInfo.versionCode;
        String permissions = appInfo.permission;
        String versionName = packageInfo.versionName;
        String packageName = packageInfo.packageName + ".apk";
        String appName = packageManager.getApplicationLabel(appInfo).toString();

        mInfo.setAttribute("name",appName);
        mInfo.setAttribute("package",packageName);
        mInfo.setAttribute("versionCode",versionCode);
        mInfo.setAttribute("versionName",versionName);
        mInfo.setAttribute("permissions",permissions);
        mInfo.setAttribute("targetSdkVersion",targetSdkVersion);
        mInfo.setAttribute("icon",icon);
        return mInfo;
    }
    /*
    public HashMap<String,Object> drawPackItems(){
        HashMap<String,Object> mPackItems = new HashMap<>();

        if(!mPackItems.isEmpty()){
            return mPackItems;
        }

        PackageManager pm = mContext.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(mFilePath,PackageManager.GET_ACTIVITIES);

        if(mInfo!=null){
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            Drawable icon = pm.getApplicationIcon(appInfo);
            int targetSdkVersion = appInfo.targetSdkVersion;
            int minSdkVersion = appInfo.minSdkVersion;
            int versionCode = packageInfo.versionCode;
            long appSize = new File(appInfo.publicSourceDir).length();
            String appSizeStr = String.format("%.2f",1.0*appSize/(1024*1024));
            String permission = appInfo.permission;
            String versionName = packageInfo.versionName;
            String packageName = packageInfo.packageName;
            String appName = pm.getApplicationLabel(appInfo).toString();

            mPackItems.put("名称",appName);
            mPackItems.put("包名",packageName);
            mPackItems.put("VersionCode",Integer.toString(versionCode));
            mPackItems.put("VersionName",versionName);
            mPackItems.put("大小",appSizeStr);
            mPackItems.put("TargetSdkVersion",Integer.toString(targetSdkVersion));
            mPackItems.put("MinSdkVersion",Integer.toString(minSdkVersion));
            mPackItems.put("Permission",permission);
        }

        return mPackItems;

    }
    */
    public void apkInstall(String fileName){
        String mFilePath = FileUtils.DIR_PACKAGE + fileName;
        String command = "chmod 777 " + mFilePath;
        Runtime runtime = Runtime.getRuntime();
        try{
            runtime.exec(command);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + mFilePath),"application/vnd.android.package-archive");
            mContext.startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
