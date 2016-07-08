package com.example.liangweiwu.downloadmanager.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;

import com.example.liangweiwu.downloadmanager.model.GameInformation;
import com.example.liangweiwu.downloadmanager.services.MyAccessibilityService;
import com.example.liangweiwu.downloadmanager.utils.AccessibilityServiceUtils;
import com.example.liangweiwu.downloadmanager.utils.FileUtils;
import com.example.liangweiwu.downloadmanager.utils.GameInformationUtils;


public class ApkInfoAccessor {
    private static ApkInfoAccessor mAccessor;
    private Context mContext;
    private PackageManager packageManager;
    private static String serviceStr;

    private ApkInfoAccessor(Context context){
        this.mContext = context;
        this.packageManager = context.getPackageManager();
    }
    public static void init(Context context){
        if(mAccessor == null){
            mAccessor = new ApkInfoAccessor(context.getApplicationContext());
        }
        serviceStr = context.getPackageName()+"/"+ MyAccessibilityService.class.getCanonicalName();
    }
    public static ApkInfoAccessor getInstance(){
        return mAccessor;
    }
    
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
        String versionName = packageInfo.versionName;
        String packageName = packageInfo.packageName;

        String appName = packageManager.getApplicationLabel(appInfo).toString();

        mInfo.setAttribute("package",fileName);
        mInfo.setAttribute("name",appName);
        mInfo.setAttribute("packageName",packageName);
        mInfo.setAttribute("versionCode",versionCode);
        mInfo.setAttribute("versionName",versionName);
        mInfo.setAttribute("targetSdkVersion",targetSdkVersion);
        mInfo.setAttribute("icon",icon);

        String[] pms = null;
        try {
            Intent query = new Intent(Intent.ACTION_MAIN);
            query.addCategory("android.intent.category.LAUNCHER");
            pms = packageManager.getPackageInfo(packageManager.queryIntentActivities(query, PackageManager
                            .MATCH_DEFAULT_ONLY).get(0).activityInfo.packageName,
                            PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        if (pms != null) {
            for (String str : pms) {
                sb.append(str+"\n");
            }
        }
        mInfo.setAttribute("permission",sb.toString());


        return mInfo;
    }
    

    public void apkInstall(String fileName){
        if(fileName == null || fileName.equals("")){
            return;
        }
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

    public void apkInstallAttempt(String fileName){
        apkInstall(fileName);
        /*
        if(!AccessibilityServiceUtils.checkAccessibilitySettingState(mContext,serviceStr)){
            onOpenSmart();
        }
        else{
            apkInstall(fileName);
        }
        */
    }

    public void onOpenSmart(){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void launchApp(String packageName){
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
