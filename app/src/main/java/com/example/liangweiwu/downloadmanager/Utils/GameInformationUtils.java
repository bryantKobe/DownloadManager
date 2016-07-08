package com.example.liangweiwu.downloadmanager.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.helper.ApkInfoAccessor;
import com.example.liangweiwu.downloadmanager.helper.GmDBHelper;
import com.example.liangweiwu.downloadmanager.model.GameInformation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class GameInformationUtils {
    private static GameInformationUtils mGameInfoUtils;
    private Context mContext;
    private GmDBHelper mDBHelper;
    private HashMap<Integer,GameInformation> mGameInfoMap;


    private GameInformationUtils(Context context){
        mContext = context;
        mDBHelper = GmDBHelper.getGmDBhelper(mContext);
        mGameInfoMap = new HashMap<>();
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
    public void onCreate(){
        loadLocalApk();
        //initData();
    }
    public void loadLocalApk(){
        mGameInfoMap = mDBHelper.query();
        for(GameInformation info : mGameInfoMap.values()){
            if(info.isDownloaded()){
                String fileName = (String)info.getAttribution("package");
                ApkInfoAccessor.getInstance().drawPackages(fileName,info);
            }
        }
        initMaxId();
        File dir = new File(FileUtils.DIR_PACKAGE);
        if(dir.exists()){
            File[] files = dir.listFiles();
            for(File file : files){
                boolean isFind = false;
                for(GameInformation info : mGameInfoMap.values()){
                    String fileName = (String) info.getAttribution("package");
                    if(fileName.equals(file.getName()) && info.isDownloaded()){
                        isFind = true;
                        break;
                    }
                }
                if(!isFind){
                    ApkInfoAccessor.getInstance().drawPackages(file.getName(),null);
                }
            }
        }
    }
    private void initMaxId(){
        for(int key : mGameInfoMap.keySet()){
            if(key > GameInformation.MAX_ID){
                GameInformation.MAX_ID = key;
            }
        }
        GameInformation.MAX_ID += 1;
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
        ArrayList<GameInformation> list = new ArrayList<>();
        for(GameInformation info : mGameInfoMap.values()){
            if(list.size() == 0){
                list.add(info);
                continue;
            }
            if(info.isDownloaded()){
                list.add(info);
                continue;
            }
            boolean isInsert = false;
            for(int i = 0 ; i < list.size(); i++){
                if(list.get(i).isDownloaded()){
                    list.add(i,info);
                    isInsert = true;
                    break;
                }
                if(info.getID() < list.get(i).getID()){
                    list.add(i,info);
                    isInsert = true;
                    break;
                }
            }
            if(!isInsert){
                list.add(info);
            }
        }
        return list;

        //return new ArrayList<>(mGameInfoMap.values());
    }
    public ArrayList<GameInformation> getDownloadedGamelist(){
        ArrayList<GameInformation> list = new ArrayList<>();
        for(GameInformation info : mGameInfoMap.values()){
            if(info.isDownloaded()){
                list.add(info);
            }
        }
        return list;
    }
    public GameInformation getGameInfoByID(int id){
        return mGameInfoMap.get(id);
    }
    public Drawable getLatestDownloadedApkIcon(){
        int curMaxID = GameInformation.EMPTY_ID;
        Drawable drawable = null;
        for(GameInformation info : mGameInfoMap.values()){
            if(!info.isDownloaded()){
                continue;
            }
            if(info.getIcon() == null){
                continue;
            }
            if(info.getID() > curMaxID){
                curMaxID = info.getID();
                drawable = info.getIcon();
            }
        }
        return drawable;
    }
    public int setApkInstalled(String packageName){
        int id = GameInformation.EMPTY_ID;
        if(packageName == null || packageName.equals("")){
            return id;
        }
        ArrayList<GameInformation> list = getDownloadedGamelist();
        for(GameInformation info : list){
            String temp = (String) info.getAttribution("packageName");
            if(temp == null || temp.equals("")){
                continue;
            }
            if(temp.trim().equals(packageName.trim())){
                info.setInstalled();
                id = info.getID();
                break;
            }
        }
        return id;
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
    public void onDownloadedFinish(GameInformation info){
        if(info == null){
            return;
        }
        if(info.getIcon() == null){
            ApkInfoAccessor.getInstance().drawPackages((String)info.getAttribution("package"),info);
        }
        GameParamUtils.getInstance().delete(info.getID());
    }
    public void delete(int id){
            GameParamUtils.getInstance().delete(id);
            mGameInfoMap.remove(id);
            mDBHelper.delete(id);
    }
    private void saveToStorage(){
        Log.d("app","save");
        mDBHelper.insert(mGameInfoMap.values());
    }
    public void clear(){
        mDBHelper.delete_all();
        mGameInfoMap.clear();
    }
    public void debug(){
        for(GameInformation info : mGameInfoMap.values()){
            info.debug();
        }
    }
}
