package com.example.liangweiwu.downloadmanager;

import android.support.v4.util.Pair;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;


public class GameInformation {
    private int mId;
    private String mName = "";
    private int mIcon = 0;
    private HashMap<String,Object> mAttributeSet = new HashMap<>();

    public GameInformation(){

    }
    public GameInformation(int ID,String name,int icon){
        this.mId = ID;
        this.mName = name;
        this.mIcon = icon;
    }
    public int getID(){
        return mId;
    }
    public String getName(){
        return mName;
    }
    public int getIcon(){
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
            this.mIcon = (int)value;
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
        Log.i("icon",String.valueOf(mIcon));
        for(String field:mAttributeSet.keySet()){
            Object value = mAttributeSet.get(field);
            if(value == null || String.valueOf(value).equals("")){
                continue;
            }
            Log.i(field,String.valueOf(value));
        }
    }
}
