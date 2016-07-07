package com.example.liangweiwu.downloadmanager.Helper;

import android.os.Handler;
import android.os.Message;
import com.example.liangweiwu.downloadmanager.Model.GameInformation;
import com.example.liangweiwu.downloadmanager.Utils.GameInformationUtils;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class UrlChecker extends Thread {
    private Handler mHandler;
    private String mUrlStr;
    public static final int URL_VALID = 1;
    public static final int URL_INVALID = 2;

    public UrlChecker(Handler mHandler, String mUrlStr) {
        this.mHandler = mHandler;
        this.mUrlStr = mUrlStr;
    }

    @Override
    public void run() {
        int length = urlCheck();

        Message msg = Message.obtain();
        if(length >= 0){
            msg.what = URL_VALID;
            msg.arg1 = length;
            msg.obj = mUrlStr;
        }else{
            msg.what = URL_INVALID;
        }
        mHandler.sendMessage(msg);
    }

    public int urlCheck() {
        if (duplicationCheck() && memoryCheck()) {
            return validationCheck();
        } else {
            return -1;
        }

    }

    public int validationCheck() {
        int length;
        try {
            URLConnection conn = new URL(mUrlStr).openConnection();
            length = conn.getContentLength();
        } catch (Exception e) {
            e.printStackTrace();
            length = -1;
        }
        return length;
    }


    public boolean duplicationCheck() {
        boolean isDuplication = false;
        ArrayList<GameInformation> list = GameInformationUtils.getInstance().getGameList();
        for(GameInformation info : list){
            String url = (String)info.getAttribution("url");
            if(url == null){
                continue;
            }
            if(url.trim().equals(mUrlStr.trim())){
                isDuplication = true;
                break;
            }
        }
        return !isDuplication;
    }
    public boolean memoryCheck(){
        return true;
    }
}
