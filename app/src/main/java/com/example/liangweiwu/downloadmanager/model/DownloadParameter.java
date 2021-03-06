package com.example.liangweiwu.downloadmanager.model;


import android.util.Log;

/**
 *  Created by Nol
 */
public class DownloadParameter {
    public static final int THREAD_STATUS_UNCOMPLETED = 0;
    public static final int THREAD_STATUS_COMPLETED = 1;
    private int ID = -1;
    private int thread_id = 0;
    private int thread_blockSize;
    private int thread_downloadedLength = 0;
    private int thread_status = THREAD_STATUS_UNCOMPLETED;

    public DownloadParameter(int id, int thread_id){
        this.ID = id;
        this.thread_id = thread_id;
    }
    public DownloadParameter(int id, int thread_id, int thread_status, int thread_blockSize, int thread_downloadedLength){
        this.ID = id;
        this.thread_id = thread_id;
        this.thread_status = thread_status;
        this.thread_blockSize = thread_blockSize;
        this.thread_downloadedLength = thread_downloadedLength;
    }
    public int getID(){
        return ID;
    }
    public int getThread_id(){
        return thread_id;
    }
    public int getThread_blockSize(){
        return thread_blockSize;
    }
    public int getThread_downloadedLength(){
        return thread_downloadedLength;
    }
    public int getThread_status(){
        return thread_status;
    }
    public boolean isCompleted(){
        return thread_status == THREAD_STATUS_COMPLETED;
    }
    public void setCompleted(){
        thread_status = THREAD_STATUS_COMPLETED;
    }
    public void setThreadStatus(int status){
        this.thread_status = status;
    }
    public void setThread_blockSize(int blockSize){
        this.thread_blockSize = blockSize;
    }
    public void update(int downloadedLength){
        this.thread_downloadedLength = downloadedLength;
    }

    public void debug(){
        Log.d("ID",String.valueOf(ID));
        Log.d("thread_id",String.valueOf(thread_id));
        Log.d("downloadedLength",String.valueOf(thread_downloadedLength));
        Log.d("blockSize",String.valueOf(thread_blockSize));
        Log.d("status",String.valueOf(thread_status));
    }
}
