package com.example.liangweiwu.downloadmanager.model;

import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.activitys.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;


public class GameInformation {
    private int mId = EMPTY_ID;
    private String mName = "正在加载...";
    private Drawable mIcon = null;
    private HashMap<String,Object> mAttributeSet = new HashMap<>();
    public static int MAX_ID = 0;
    public final static int EMPTY_ID = 0;

    public static final int PACKAGE_STATUS_DOWNLOADING = 0;
    public static final int PACKAGE_STATUS_DOWNLOADED = 1;
    public static final int PACKAGE_STATUS_INSTALLED = 2;


    public static String getFilename(String url){
        if(url == null || url.equals("")){
            return "";
        }
        return url.substring(url.lastIndexOf('/') + 1);
    }
    public GameInformation(){
    }
    public GameInformation(String type){
        onCreate();
        if(type == null || type.equals("") || type.equals("empty")){

        }else{
            this.mId = MAX_ID;
            MAX_ID++;
            if(type.equals("local")){
                mAttributeSet.put("status",PACKAGE_STATUS_DOWNLOADED);
            }
        }
    }
    public GameInformation(String url,int thread_num){
        this.mId = MAX_ID;
        MAX_ID++;
        mAttributeSet.put("url",url);
        mAttributeSet.put("package",getFilename(url));
        onCreate();
        if(thread_num > 1){
            mAttributeSet.put("thread_number",thread_num);
        }
    }

    private void onCreate(){
        mAttributeSet.put("thread_number", MainActivity.DEFAULT_THREAD_COUNT);
        mAttributeSet.put("status",PACKAGE_STATUS_DOWNLOADING);
    }

    public int getID(){
        return mId;
    }
    public String getName(){
        return mName;
    }
    public Drawable getIcon(){
        return mIcon;
    }
    public int getThreadNumber(){
        return (int)mAttributeSet.get("thread_number");
    }
    public String getUrl(){
        return (String)mAttributeSet.get("url");
    }
    public void setStatus(int st){
        mAttributeSet.put("status",st);
    }
    public int getStatus(){
        return (int)mAttributeSet.get("status");
    }
    public void setDownloaded(){
        mAttributeSet.put("status",PACKAGE_STATUS_DOWNLOADED);
    }
    public void setInstalled(){
        mAttributeSet.put("status",PACKAGE_STATUS_INSTALLED);
    }
    public boolean isDownloaded(){
        return (int)mAttributeSet.get("status") > 0;
    }
    public boolean isInstalled(){
        return (int)mAttributeSet.get("status") == PACKAGE_STATUS_INSTALLED;
    }
    public String getFileName(){
        return (String) mAttributeSet.get("package");
    }
    public String getPackageName(){
        return (String) mAttributeSet.get("packageName");
    }
    public void setAttribute(String field,Object value){
        if(value == null){
            return;
        }
        if(value instanceof String && value.equals("")){
            return;
        }
        if(field.equals("ID")){
            this.mId = (int)value;
            return;
        }
        if(field.equals("name")){
            this.mName = (String)value;
            return;
        }
        if(field.equals("icon")){
            this.mIcon = (Drawable)value;
        }
        mAttributeSet.put(field,value);
    }
    public Object getAttribution(String field){
        return mAttributeSet.get(field);
    }
    public ArrayList<Pair<String,String>> getAttributions(){
        ArrayList<Pair<String,String>> list = new ArrayList<>();
        list.add(new Pair<>("名称",mName));
        for(String field : mAttributeSet.keySet()){
            list.add(new Pair<>(field,String.valueOf(mAttributeSet.get(field))));
        }
        return list;
    }
    public void debug(){
        Log.i("ID",String.valueOf(mId));
        Log.i("name",mName);
        for(String field:mAttributeSet.keySet()){
            Object value = mAttributeSet.get(field);
            if(value == null || String.valueOf(value).equals("")){
                continue;
            }
            Log.i(field,String.valueOf(value));
        }
    }
}
