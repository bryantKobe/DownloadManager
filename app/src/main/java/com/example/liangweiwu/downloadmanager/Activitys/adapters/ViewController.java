package com.example.liangweiwu.downloadmanager.activitys.adapters;

import com.example.liangweiwu.downloadmanager.model.DownloadTaskController;

/**
 * Created by Nol on 2016/7/11.
 */
public class ViewController{
    public static final int PARAMS_LENGTH = 3;
    private DownloadTaskController controller = null;
    private Integer[] params = new Integer[PARAMS_LENGTH];
    private boolean isFinish = false;
    public ViewController(){
        for(int i = 0 ; i < PARAMS_LENGTH; i++){
            params[i] = 0;
            int j = 0;
        }
    }
    public void setController(DownloadTaskController controller){
        this.controller = controller;
    }
    public void updateParams(Integer[] params){
        for(int i = 0 ; i < PARAMS_LENGTH; i++){
            this.params[i] = params[i];
        }
    }
    public DownloadTaskController getController(){
        return controller;
    }
    public int getDownloadedSize(){
        return params[0];
    }
    public int getSpeed(){
        return params[1];
    }
    public int getFileSize(){
        return params[2];
    }
    public int getInfoID(){
        return controller.getInfo().getID();
    }
}
