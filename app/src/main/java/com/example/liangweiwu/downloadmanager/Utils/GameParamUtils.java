package com.example.liangweiwu.downloadmanager.Utils;


import android.content.Context;
import com.example.liangweiwu.downloadmanager.Model.DownloadParam;

import java.util.ArrayList;
import java.util.HashMap;

public class GameParamUtils {
    private static GameParamUtils mGameParamUtils;
    private Context mContext;
    private HashMap<Integer,DownloadParam[]> mParamMap;

    private GameParamUtils(Context context){
        this.mContext = context.getApplicationContext();
        onCreate();
    }
    public static void init(Context context){
        if(mGameParamUtils == null){
            mGameParamUtils = new GameParamUtils(context);
        }
    }
    public static GameParamUtils getInstance(){
        return mGameParamUtils;
    }
    private void onCreate(){
        mParamMap = new HashMap<>();
    }
    public void onDestroy(){

    }
    public void saveParams(DownloadParam[] params){
        if(params == null){
            return;
        }
        int id = params[0].getID();
        mParamMap.put(id,params);
    }
    public DownloadParam[] getParams(int id){
        return mParamMap.get(id);
    }
}
