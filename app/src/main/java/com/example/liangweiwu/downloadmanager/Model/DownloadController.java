package com.example.liangweiwu.downloadmanager.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.liangweiwu.downloadmanager.Utils.NetworkUtils;

public class DownloadController extends BroadcastReceiver{
    private DownloadTask mDownloadTask;
    private GameInformation info;
    private DownloadParam[] params;
    public DownloadController(){}
    public DownloadController(String url, int threadNum) throws Exception{
        mDownloadTask = new DownloadTask(url,threadNum);
        info = mDownloadTask.getInfo();
        params = mDownloadTask.getParams();
    }
    public DownloadController(GameInformation info, DownloadParam[] params) throws Exception{
        mDownloadTask = new DownloadTask(info,params);
        this.info = info;
        this.params = params;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        this.stop();
        boolean isConnected = NetworkUtils.getInstance().isNetworkAvailable();
        if(isConnected){
            this.resume();
        }else{
            Toast.makeText(context, "网络中断，请检查网络连接！",Toast.LENGTH_SHORT).show();
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
            mDownloadTask = new DownloadTask(info,params);
            mDownloadTask.Start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
