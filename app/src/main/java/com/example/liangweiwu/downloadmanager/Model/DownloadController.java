package com.example.liangweiwu.downloadmanager.Model;

import android.view.View;


public abstract class DownloadController {
    private DownloadTask mDownloadTask = null;
    private GameInformation info;
    private DownloadParam[] params;
    private View mItemView;
    public DownloadController(String url, int threadNum){
        try {
            mDownloadTask = newTask(url,threadNum);
            info = mDownloadTask.getInfo();
            params = mDownloadTask.getParams();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public DownloadController(GameInformation info, DownloadParam[] params){
        try {
            mDownloadTask = newTask(info,params);
            this.info = info;
            this.params = params;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     *  开始下载任务，若已经开始，则不起作用
     */
    public void start(){
        mDownloadTask.Start();
    }
    public void pause(){
        mDownloadTask.Pause();
    }
    public void resume(){
        mDownloadTask.Resume();
    }
    public void stop(){
        mDownloadTask.Stop();
        /*
        info.debug();
        for(int i = 0 ; i < params.length; i++){
            params[i].debug();
        }
        */
    }
    public void restart(){
        if(mDownloadTask.getDownloadState() != DownloadTask.DOWNLOAD_STATE_TERMINATED
                && mDownloadTask.getDownloadState() != DownloadTask.DOWNLOAD_STATE_FAILED){
            return;
        }
        try{
            mDownloadTask = newTask(info,params);
            mDownloadTask.Start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private DownloadTask newTask(String url, int threadNum) throws Exception{
        return new DownloadTask(url,threadNum){
            @Override
            protected void onStart(Integer... values){
                initViews(values);
            }
            @Override
            protected void onStop(){
                onDownloadStop();
            }
            @Override
            protected void onUpdate(Integer... values){
                bindViews(values);
            }
        };
    }
    private DownloadTask newTask(GameInformation info, DownloadParam[] params)throws Exception{
        return new DownloadTask(info,params){
            @Override
            protected void onStart(Integer... values){
                initViews(values);
            }
            @Override
            protected void onStop(){
                onDownloadStop();
            }
            @Override
            protected void onUpdate(Integer... values){
                bindViews(values);
            }
        };
    }
    public GameInformation getInfo(){
        return info;
    }
    public int getDownloadState(){
        return mDownloadTask.getDownloadState();
    }

    public abstract void initViews(Integer... values);
    public abstract void bindViews(Integer... values);
    public abstract void onDownloadStop();
    public void debug(){
        info.debug();
        for(int i = 0 ; i < params.length; i++){
            params[i].debug();
        }
    }
}
