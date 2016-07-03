package com.example.liangweiwu.downloadmanager.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.Pair;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.Utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class GameInformation {
    private int mId;
    private String mName = "正在加载...";
    private String mIcon = "default";
    private HashMap<String,Object> mAttributeSet = new HashMap<>();
    public static int MAX_ID = 0;

    public GameInformation(){
        mAttributeSet.put("thread_number",1);
        this.mId = MAX_ID;
        MAX_ID++;
    }
    public GameInformation(int ID,String name,String icon){
        this.mId = ID;
        this.mName = name;
        this.mIcon = icon;
        mAttributeSet.put("thread_number",1);
    }
    public int getID(){
        return mId;
    }
    public String getName(){
        return mName;
    }
    public Bitmap getIconBtimap(Context context){
        Bitmap bitmap;
        if(mIcon.equals("default")){
            bitmap = FileUtils.getBitmap(context, R.drawable.default_icon);
        }else{
            bitmap = FileUtils.getBitmap(context, mIcon);
        }
        return bitmap;
    }
    public String getIcon(){
        return mIcon;
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
            this.mIcon = (String)value;
            return;
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
        Log.i("icon",mIcon);
        for(String field:mAttributeSet.keySet()){
            Object value = mAttributeSet.get(field);
            if(value == null || String.valueOf(value).equals("")){
                continue;
            }
            Log.i(field,String.valueOf(value));
        }
    }
}
