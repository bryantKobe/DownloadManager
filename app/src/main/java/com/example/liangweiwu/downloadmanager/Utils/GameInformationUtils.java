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
        mContext = context;
        onCreate();
    }
    /*
     *  获取Util单例
     */
    public static GameInformationUtils getInstance(){
        return mGameInfoUtils;
    }
    /*
     *  初始化
     */
    public static void init(Context context){
        if(mGameInfoUtils==null){
            mGameInfoUtils = new GameInformationUtils(context.getApplicationContext());
        }
    }
    private void onCreate(){
        mDBHelper = GmDBHelper.getGmDBhelper(mContext);
        //initData();
        mGameInfoMap = mDBHelper.query();
        initMaxId();
    }
    /*
     *  销毁
     */
    public void onDestroy(){
        saveToStorage();
        mDBHelper.close();
    }
    /*
     *   TODO
     */
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
    public GameInformation createGameInfo(String url,int thread_number){
        GameInformation info = new GameInformation(url,thread_number);
        mGameInfoMap.put(info.getID(),info);
        return info;
    }
    public GameInformation createGameInfo(){
        GameInformation info = new GameInformation("new");
        mGameInfoMap.put(info.getID(),info);
        return info;
    }
    private void saveToStorage(){
        System.out.println("store");
        mDBHelper.insert(mGameInfoMap.values());
    }
}
