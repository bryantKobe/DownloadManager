package com.example.liangweiwu.downloadmanager.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.Helper.ApkInfoAccessor;

import java.io.File;


public class FileUtils {
    public static String DIR_ICON;
    public static String DIR_PACKAGE;
    public static String DIR_PACKAGE_INTERNAL;
    public static String DIR_PACKAGE_EXTERNAL;
    public static void init(Context context){
        DIR_ICON = context.getFilesDir().getPath()+"/icon/";
        File dir_icon = new File(DIR_ICON);
        if(!dir_icon.exists()){
            dir_icon.mkdirs();
        }
        try{
            DIR_PACKAGE_EXTERNAL = context.getExternalFilesDir(null).getPath()+"/package/";
            DIR_PACKAGE = DIR_PACKAGE_EXTERNAL;
        }catch (Exception e){
            Log.i("package dir","internal");
            DIR_PACKAGE_INTERNAL = context.getFilesDir().getPath() + "/package/";
            DIR_PACKAGE = DIR_PACKAGE_INTERNAL;
        }finally{
            File dir_package = new File(DIR_PACKAGE);
            if(!dir_package.exists()){
                Log.i("directory","make");
                dir_package.mkdirs();
            }
        }
    }
    public void rename(String packageName,String fileName){

    }

    public static boolean deleteApk(String fileName){
        File apk = new File(DIR_PACKAGE + fileName);
        if(apk.exists()){
            return apk.delete();
        }
        return true;
    }
    public static Bitmap getBitmap(Context context,int resId){
        Bitmap bitmap = null;
        try{
            bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(resId));
        }catch (Exception e){
            Log.e("bitmap","decode resiD failed!");
        }
        return bitmap;
    }
    public static Bitmap getBitmap(Context context,String resName){
        Bitmap bitmap = null;
        try{
            bitmap = BitmapFactory.decodeStream(context.openFileInput(resName));
        }catch (Exception e){
            Log.e("bitmap","decode resName failed");
        }
        return bitmap;
    }
}
