package com.example.liangweiwu.downloadmanager.util;


import android.content.Context;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.model.DownloadParameter;
import com.example.liangweiwu.downloadmanager.model.ApkInformation;

import java.util.HashMap;
/**
 *  Created by Nol
 */
public class DownloadParameterUtils {
    private static DownloadParameterUtils mDownloadParameterUtils;
    private Context mContext;
    private DBHelper mDBHelper;
    private HashMap<Integer,DownloadParameter[]> mParamMap;

    private DownloadParameterUtils(Context context){
        this.mContext = context.getApplicationContext();
        mDBHelper = DBHelper.getGmDBhelper(mContext);
    }
    public static void init(Context context){
        if(mDownloadParameterUtils == null){
            mDownloadParameterUtils = new DownloadParameterUtils(context);
        }
    }
    public static DownloadParameterUtils getInstance(){
        return mDownloadParameterUtils;
    }
    public void onCreate(){
        mParamMap = mDBHelper.query_param();
    }
    public void onDestroy(){
        saveToStorage();
        mDBHelper.close();
    }

    public DownloadParameter[] createParams(ApkInformation info){
        if(info.isDownloaded()){
            return null;
        }
        int id = info.getID();
        int thread_number = info.getThreadNumber();
        DownloadParameter[] params = new DownloadParameter[thread_number];
        for(int i = 0 ;i < thread_number; i++){
            params[i] = new DownloadParameter(id,i);
        }
        mParamMap.put(info.getID(),params);
        return params;
    }
    public void saveParams(DownloadParameter[] params){
        if(params == null){
            return;
        }
        int id = params[0].getID();
        mParamMap.put(id,params);
    }
    public HashMap<Integer,DownloadParameter[]> getParamMap(){
        return mParamMap;
    }
    public DownloadParameter[] getParams(int id){
        return mParamMap.get(id);
    }
    private void saveToStorage(){
        Log.d("app","parameters save");
        mDBHelper.insert_params(mParamMap.values());
    }
    public void delete(int id){
        mParamMap.remove(id);
        mDBHelper.delete_param(id);
    }
    public void clean(){

    }
    public void debug(){
        for(DownloadParameter[] params : mParamMap.values()){
            for(DownloadParameter param : params){
                param.debug();
            }
        }
    }
}
