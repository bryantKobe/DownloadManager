package com.example.liangweiwu.downloadmanager;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;


public class GameInformationUtils {
    private static GameInformationUtils mGameInfoUtils;
    private Context mContext;
    private GmDBHelper mDBHelper;
    private HashMap<Integer,GameInformation> mGameInfoMap;

    private GameInformationUtils(Context context){
        mContext = context;
        onCreate();
    }
    /*
     *  获取Util单例
     */
    public static GameInformationUtils getInstance(Context context){
        if(mGameInfoUtils==null){
            mGameInfoUtils = new GameInformationUtils(context.getApplicationContext());
        }
        return mGameInfoUtils;
    }
    /*
     *  初始化
     */
    private void onCreate(){
        mDBHelper = GmDBHelper.getGmDBhelper(mContext);
        mGameInfoMap = mDBHelper.query();
    }
    /*
     *  销毁
     */
    public void onDestory(){
        mDBHelper.close();
    }
    /*
     *   TODO
     */
    public ArrayList<GameInformation> getGameList(){
        return new ArrayList<>(mGameInfoMap.values());
    }
    public GameInformation getGameInfoByID(int id){
        return mGameInfoMap.get(id);
    }
}
