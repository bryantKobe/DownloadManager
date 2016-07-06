package com.example.liangweiwu.downloadmanager.Utils;

import android.content.Context;
import android.util.Log;
import com.example.liangweiwu.downloadmanager.Helper.GmDBHelper;
import com.example.liangweiwu.downloadmanager.Model.DownloadParam;
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
        GameParamUtils.getInstance().createParams(info);
        return info;
    }
    public GameInformation createGameInfo(String type){
        GameInformation info = new GameInformation(type);
        if(info.getID() != GameInformation.EMPTY_ID){
            mGameInfoMap.put(info.getID(),info);
        }
        return info;
    }
    private void saveToStorage(){
        System.out.println("store");
        mDBHelper.insert(mGameInfoMap.values());
    }
    public void clear(){
        mDBHelper.delete_all();
        mGameInfoMap.clear();
    }
    public void debug(){
        mDBHelper.delete_param(1);
        for(DownloadParam params[] : mDBHelper.query_param().values()){
            for(DownloadParam param : params){
                param.debug();
            }
        }
    }
}
