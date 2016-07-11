package com.example.liangweiwu.downloadmanager.utils;

import com.example.liangweiwu.downloadmanager.activitys.events.MainUiEvent;
import com.example.liangweiwu.downloadmanager.model.ApkInformation;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class UrlChecker extends Thread {
    private String mUrlStr;
    public static final int URL_VALID = 1;
    public static final int URL_INVALID = 2;

    public UrlChecker( String mUrlStr) {
        this.mUrlStr = mUrlStr;
    }

    @Override
    public void run() {
        int length = urlCheck();

        int what = (length >= 0 ? MainUiEvent.EVENT_URL_VALID : MainUiEvent.EVENT_URL_INVALID);
        MainUiEvent event = MainUiEvent.createUrlCheckEvent(what,mUrlStr,length);
        EventBus.getDefault().post(event);
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
        ArrayList<ApkInformation> list = GameInformationUtils.getInstance().getGameList();
        for(ApkInformation info : list){
            String url = info.getUrl();
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
