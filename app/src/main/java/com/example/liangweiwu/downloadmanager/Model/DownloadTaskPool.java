package com.example.liangweiwu.downloadmanager.Model;

import android.os.Handler;

import com.example.liangweiwu.downloadmanager.Activitys.MainActivity;
import com.example.liangweiwu.downloadmanager.Helper.DownloadItemAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liangwei.wu on 16/7/7.
 */
public class DownloadTaskPool extends Thread{
    public static final int MAX_PARALLEL_THREAD_COUNT = 2;

    private static ExecutorService exec = Executors.newFixedThreadPool(MAX_PARALLEL_THREAD_COUNT);
    private ArrayList<DownloadItemAdapter.UpdateParams> mBlockingQueue;
    private ArrayList<DownloadItemAdapter.UpdateParams> mRunningQueue;
    private Handler mHandler;
    private int current_downloadTask_count;
    private boolean isRunning = true;

    public static ExecutorService getExec(){
        return exec;
    }
    public DownloadTaskPool(Handler handler){
        mBlockingQueue = new ArrayList<>();
        mRunningQueue = new ArrayList<>();
        this.mHandler = handler;
        current_downloadTask_count = 0;
    }
    public void addTask(DownloadItemAdapter.UpdateParams params){
        if(params.isFinish()){
            return;
        }
        mBlockingQueue.add(params);
    }
    public void cancelTask(DownloadItemAdapter.UpdateParams params){
        if(params.isFinish()){
            return;
        }
        mBlockingQueue.remove(params);
    }

    @Override
    public void run(){
        while(isRunning){
            try {
                for(Iterator<DownloadItemAdapter.UpdateParams> it = mRunningQueue.iterator();it.hasNext();){
                    if(it.next().isFinish()){
                        mRunningQueue.remove(it.next());
                        current_downloadTask_count --;
                    }
                }
                if(current_downloadTask_count < MAX_PARALLEL_THREAD_COUNT){
                    if(mBlockingQueue.size() > 0){
                        DownloadItemAdapter.UpdateParams params = mBlockingQueue.remove(0);
                        //params.getController().start();
                        sendMessage(params.getInfoID());
                        mRunningQueue.add(params);
                        current_downloadTask_count++;
                    }
                }
                //System.out.println(mBlockingQueue.size());
                //System.out.println(mRunningQueue.size());
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void Stop(){
        isRunning = false;
    }
    private void sendMessage(int id){
        mHandler.sendMessage(mHandler.obtainMessage(100,id));
    }
}
