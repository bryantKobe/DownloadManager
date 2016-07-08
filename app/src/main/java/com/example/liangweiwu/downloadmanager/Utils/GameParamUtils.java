package com.example.liangweiwu.downloadmanager.utils;


import android.content.Context;

import com.example.liangweiwu.downloadmanager.helper.GmDBHelper;
import com.example.liangweiwu.downloadmanager.model.DownloadParam;
import com.example.liangweiwu.downloadmanager.model.GameInformation;

import java.util.HashMap;

public class GameParamUtils {
    private static GameParamUtils mGameParamUtils;
    private Context mContext;
    private GmDBHelper mDBHelper;
    private HashMap<Integer,DownloadParam[]> mParamMap;

    private GameParamUtils(Context context){
        this.mContext = context.getApplicationContext();
        mDBHelper = GmDBHelper.getGmDBhelper(mContext);
    }
    public static void init(Context context){
        if(mGameParamUtils == null){
            mGameParamUtils = new GameParamUtils(context);
        }
    }
    public static GameParamUtils getInstance(){
        return mGameParamUtils;
    }
    public void onCreate(){
        mParamMap = mDBHelper.query_param();
    }
    public void onDestroy(){
        saveToStorage();
        mDBHelper.close();
    }

    public DownloadParam[] createParams(GameInformation info){
        if((Integer)info.getAttribution("status") == 1){
            return null;
        }
        int id = info.getID();
        int thread_number = (Integer)info.getAttribution("thread_number");
        DownloadParam[] params = new DownloadParam[thread_number];
        for(int i = 0 ;i < thread_number; i++){
            params[i] = new DownloadParam(id,i);
        }
        mParamMap.put(info.getID(),params);
        return params;
    }
    public void saveParams(DownloadParam[] params){
        if(params == null){
            return;
        }
        int id = params[0].getID();
        mParamMap.put(id,params);
    }
    public HashMap<Integer,DownloadParam[]> getParamMap(){
        return mParamMap;
    }
    public DownloadParam[] getParams(int id){
        return mParamMap.get(id);
    }
    private void saveToStorage(){
        System.out.println("store param");
        mDBHelper.insert_params(mParamMap.values());
    }
    public void delete(int id){
        mParamMap.remove(id);
        mDBHelper.delete_param(id);
    }
    public void clean(){

    }
    public void debug(){
        for(DownloadParam[] params : mParamMap.values()){
            for(DownloadParam param : params){
                param.debug();
            }
        }
    }
}
