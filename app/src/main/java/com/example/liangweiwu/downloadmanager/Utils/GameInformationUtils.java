package com.example.liangweiwu.downloadmanager.Utils;

import android.content.Context;
import android.util.Log;
import com.example.liangweiwu.downloadmanager.Helper.GmDBHelper;
import com.example.liangweiwu.downloadmanager.Model.GameInformation;
import java.util.ArrayList;
import java.util.HashMap;


public class GameInformationUtils {
    private static GameInformationUtils mGameInfoUtils;
    private Context mContext;
    private GmDBHelper mDBHelper;
    private HashMap<Integer,GameInformation> mGameInfoMap;


    private GameInformationUtils(Context context){
        Log.i("debug","init0");
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
    public static GameInformationUtils getInstance(){
        return mGameInfoUtils;
    }
    /*
     *  初始化
     */
    private void onCreate(){
        mDBHelper = GmDBHelper.getGmDBhelper(mContext);
        //initData();
        mGameInfoMap = mDBHelper.query();
        initMaxId();
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
    public void initData(){
        Log.i("debug","init");
        GameInformation info = new GameInformation();
        mDBHelper.insert(info);
    }
    public ArrayList<GameInformation> getGameListFromStroage(){
        ArrayList<GameInformation> list = new ArrayList<>();
        return list;
    }
    public ArrayList<GameInformation> getGameList(){
        return new ArrayList<>(mGameInfoMap.values());
    }
    private void initMaxId(){
        for(int key : mGameInfoMap.keySet()){
            if(key > GameInformation.MAX_ID){
                GameInformation.MAX_ID = key;
            }
        }
        GameInformation.MAX_ID += 1;
    }
    public GameInformation getGameInfoByID(int id){
        return mGameInfoMap.get(id);
    }
}
