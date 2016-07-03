package com.example.liangweiwu.downloadmanager.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Nol on 2016/7/2.
 */
public class NetworkUtils {
    private static NetworkUtils mNetworkUtils;
    private ConnectivityManager connectivityManager;

    private NetworkUtils(Context context){
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    public static void init(Context context){
        if(mNetworkUtils == null){
            mNetworkUtils = new NetworkUtils(context.getApplicationContext());
        }
    }
    public static NetworkUtils getInstance(){
        return mNetworkUtils;
    }
    /**
     * 网络是否可用
     */
    public boolean isNetworkAvailable() {
        try {
            if (connectivityManager != null) {
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                return (info != null && info.isConnected() && info.getState() == NetworkInfo.State.CONNECTED);
                /*
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
                */
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    /**
     * 判断当前网络是否是wifi网络
     * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //判断3G网
     */
    public boolean isWifi(){
        try{
            if(connectivityManager != null){
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
                return (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI);
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }
    /**
     * 判断当前网络是否是3G网络
     */
    public boolean is3G() {
        try{
            if(connectivityManager != null){
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
                return (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE);
            }
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
        return false;
    }
}
