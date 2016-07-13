package com.example.liangweiwu.downloadmanager.view.controller;

import com.example.liangweiwu.downloadmanager.model.DownloadTaskController;

/**
 * Created by Nol on 2016/7/11.
 */
public class ViewController{
    public static final int PARAMS_LENGTH = 3;
    private DownloadTaskController mController = null;
    private Integer[] mParams = new Integer[PARAMS_LENGTH];
    private int mPosition;
    public ViewController(){
        for(int i = 0 ; i < PARAMS_LENGTH; i++){
            mParams[i] = 0;
        }
    }
    public void setController(DownloadTaskController controller){
        this.mController = controller;
    }
    public void setPosition(int position){
        this.mPosition = position;
    }
    public void updateParams(Integer[] params){
        for(int i = 0 ; i < PARAMS_LENGTH; i++){
            this.mParams[i] = params[i];
        }
    }
    public DownloadTaskController getController(){
        return mController;
    }
    public int getPosition(){
        return mPosition;
    }
    public int getDownloadedSize(){
        return mParams[0];
    }
    public int getSpeed(){
        return mParams[1];
    }
    public int getFileSize(){
        return mParams[2];
    }
    public int getInfoID(){
        return mController.getInfo().getID();
    }
}
