package com.example.liangweiwu.downloadmanager.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.File;


public class FileUtils {
    public static String DIR_ICON;
    public static String DIR_PACKAGE;
    public static void init(Context context){
        DIR_ICON = context.getFilesDir().getPath()+"/icon/";
        File dir_icon = new File(DIR_ICON);
        if(!dir_icon.exists()){
            dir_icon.mkdirs();
        }
        try{
            DIR_PACKAGE = context.getExternalCacheDir().getPath()+"/package/";
        }catch (Exception e){
            DIR_PACKAGE = context.getFilesDir().getPath() + "/package/";
        }finally{
            File dir_package = new File(DIR_PACKAGE);
            if(!dir_package.exists()){
                dir_package.mkdirs();
            }
        }

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
