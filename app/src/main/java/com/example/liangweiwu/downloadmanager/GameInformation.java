package com.example.liangweiwu.downloadmanager;

/**
 * Created by liangwei.wu on 16/6/29.
 */
public class GameInformation {
    private String mName = "";
    private int mIcon = 0;
    public GameInformation(){
    }
    public GameInformation(String name,int icon){
        this.mName = name;
        this.mIcon = icon;
    }
    public String getName(){
        return mName;
    }
    public int getIcon(){
        return mIcon;
    }
}
