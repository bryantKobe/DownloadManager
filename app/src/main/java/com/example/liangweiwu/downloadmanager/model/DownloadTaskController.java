package com.example.liangweiwu.downloadmanager.model;

import android.support.v7.widget.RecyclerView;

import com.example.liangweiwu.downloadmanager.activitys.MainActivity;
import com.example.liangweiwu.downloadmanager.activitys.adapters.DownloadItemAdapter;
import com.example.liangweiwu.downloadmanager.model.thread.DownloadMainThread;
import com.example.liangweiwu.downloadmanager.utils.GameParamUtils;


public abstract class DownloadTaskController {
    private DownloadMainThread mDownloadTask = null;
    private ApkInformation info;
    private DownloadParameter[] params;
    private boolean isInstalled = false;

    public static DownloadItemAdapter.UpdateParams createInstance(
            String url,int thread_number,final RecyclerView.Adapter mAdapter){

        final DownloadItemAdapter.UpdateParams pp = new DownloadItemAdapter.UpdateParams();
        DownloadTaskController controller = new DownloadTaskController(url,thread_number) {
            @Override
            public void initViews(Integer... values) {
                pp.updateParams(values);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void bindViews(Integer... values) {
                pp.updateParams(values);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onDownloadStop() {
                System.out.println("stop");
                mAdapter.notifyDataSetChanged();
            }
        };
        controller.addTask();
        pp.setController(controller);
        return pp;
    }
    public static DownloadItemAdapter.UpdateParams createInstance(
            ApkInformation info, DownloadParameter[] params, final RecyclerView.Adapter mAdapter){

        final DownloadItemAdapter.UpdateParams pp = new DownloadItemAdapter.UpdateParams();
        DownloadTaskController controller = new DownloadTaskController(info,params) {
            @Override
            public void initViews(Integer... values) {
                pp.updateParams(values);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void bindViews(Integer... values) {
                pp.updateParams(values);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onDownloadStop() {
                mAdapter.notifyDataSetChanged();
            }
        };
        controller.addTask();
        pp.setController(controller);
        return pp;
    }

    public DownloadTaskController(String url, int threadNum){
        try {
            mDownloadTask = newTask(url,threadNum);
            info = mDownloadTask.getInfo();
            params = mDownloadTask.getParams();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public DownloadTaskController(ApkInformation info, DownloadParameter[] params){
        try {
            if(!info.isDownloaded()){
                mDownloadTask = newTask(info,params);
                if(params == null){
                    params = GameParamUtils.getInstance().createParams(info);
                }
            }
            isInstalled = info.isInstalled();
            this.info = info;
            this.params = params;


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     *  开始下载任务，若已经开始，则不起作用
     */
    public void addTask(){
        MainActivity.mThread_pool.addTask(this);
    }
    public void pauseTask(){
        MainActivity.mThread_pool.cancelTask(this);
    }
    public void start(){
        if(mDownloadTask == null){
            return;
        }
        mDownloadTask.Start();
    }
    public void pause(){
        if(mDownloadTask == null){
            return;
        }
        mDownloadTask.Pause();
    }
    public void resume(){
        if(mDownloadTask == null){
            return;
        }
        mDownloadTask.Resume();
    }
    public void stop(){
        if(mDownloadTask == null){
            return;
        }
        mDownloadTask.Stop();
    }
    public void restart(){
        if(mDownloadTask == null){
            return;
        }
        if(mDownloadTask.getDownloadState() != DownloadMainThread.DOWNLOAD_STATE_TERMINATED
                && mDownloadTask.getDownloadState() != DownloadMainThread.DOWNLOAD_STATE_FAILED){
            return;
        }
        try{
            mDownloadTask = newTask(info,params);
            addTask();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private DownloadMainThread newTask(String url, int threadNum) throws Exception{
        return new DownloadMainThread(url,threadNum){
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
    private DownloadMainThread newTask(ApkInformation info, DownloadParameter[] params)throws Exception{
        return new DownloadMainThread(info,params){
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
    public ApkInformation getInfo(){
        return info;
    }
    public int getDownloadState(){
        if(isInstalled){
            return DownloadMainThread.DOWNLOAD_STATE_INSTALLED;
        }
        if(mDownloadTask == null){
            return DownloadMainThread.DOWNLOAD_STATE_END;
        }
        return mDownloadTask.getDownloadState();
    }
    public void setApkInstall(){
        isInstalled = true;
        if(mDownloadTask == null){
            return;
        }
        mDownloadTask.setApkInstalled();
    }
    /*
    public int getDownloadedSize(){
        int downloadedSize = 0;
        for(DownloadParameter param : params){
            downloadedSize += param.getThread_downloadedLength();
        }
        return downloadedSize;
    }
    public int getFileSize(){
        String size = (String) info.getAttribution("size");
        if(size == null || size.equals("")){
            return 0;
        }
        return Integer.valueOf(size);
    }
    */
    public boolean isFinish(){
        return mDownloadTask == null || mDownloadTask.isFinished();
    }

    public abstract void initViews(Integer... values);
    public abstract void bindViews(Integer... values);
    public abstract void onDownloadStop();
    public void debug(){
        if(info == null){
            return;
        }
        info.debug();
        for(int i = 0 ; i < params.length; i++){
            params[i].debug();
        }
    }
}
