package com.example.liangweiwu.downloadmanager.model;

import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.thread.DownloadMainThread;

import java.util.ArrayList;
import java.util.HashMap;


public class ApkInformation {
    private int mId = EMPTY_ID;
    private String mName = "正在加载...";
    private Drawable mIcon = null;
    private String mPackage = "";
    private String mPackageName = "";
    private String mUrl = "";
    private int mStatus = PACKAGE_STATUS_DOWNLOADING;
    private int mThread_number = DownloadMainThread.DEFAULT_THREAD_COUNT;
    private int mSize = 0;
    private HashMap<String,Object> mAttributeSet = new HashMap<>();
    public static int MAX_ID = 0;
    public final static int EMPTY_ID = 0;

    public static final int PACKAGE_STATUS_DOWNLOADING = 0;
    public static final int PACKAGE_STATUS_DOWNLOADED = 1;
    public static final int PACKAGE_STATUS_INSTALLED = 2;


    public static String GetFilename(String url){
        if(url == null || url.equals("")){
            return "";
        }
        return url.substring(url.lastIndexOf('/') + 1);
    }
    public ApkInformation(){
    }
    public ApkInformation(String type){
        if(type == null || type.equals("") || type.equals("empty")){

        }else{
            this.mId = MAX_ID;
            MAX_ID++;
            if(type.equals("local")){
                mStatus = PACKAGE_STATUS_DOWNLOADED;
            }
        }
    }
    public ApkInformation(String url, int thread_num){
        this.mId = MAX_ID;
        MAX_ID++;
        mUrl = url;
        mPackage = GetFilename(url);
        if(thread_num > 1){
            mThread_number = thread_num;
        }
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
        return mThread_number;
    }
    public String getUrl(){
        return mUrl;
    }
    public void setStatus(int st){
        mStatus = st;
    }
    public int getStatus(){
        return mStatus;
    }
    public int getSize(){
        return mSize;
    }
    public void setDownloaded(){
        mStatus = PACKAGE_STATUS_DOWNLOADED;
    }
    public void setInstalled(){
        mStatus = PACKAGE_STATUS_INSTALLED;
    }
    public boolean isDownloaded(){
        return mStatus > 0;
    }
    public boolean isInstalled(){
        return mStatus == PACKAGE_STATUS_INSTALLED;
    }
    public String getFileName(){
        return mPackage;
    }
    public String getPackageName(){
        return mPackageName;
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
        }else if(field.equals("name")){
            this.mName = (String)value;
        }else if(field.equals("icon")){
            this.mIcon = (Drawable)value;
        }else if(field.equals("package")){
            this.mPackage = (String)value;
        }else if(field.equals("status")){
            mStatus = (int)value;
        }else if(field.equals("url")){
            mUrl = (String)value;
        }else if(field.equals("thread_number")){
            mThread_number = (int)value;
        }else if(field.equals("size")){
            mSize = Integer.valueOf((String)value);
        }else if(field.equals("packageName")){
            mPackageName = (String)value;
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
