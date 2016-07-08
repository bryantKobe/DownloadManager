package com.example.liangweiwu.downloadmanager.model;

import android.os.Handler;
import com.example.liangweiwu.downloadmanager.helper.DownloadItemAdapter;
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
    private ArrayList<DownloadController> mBlockingQueue;
    private ArrayList<DownloadController> mRunningQueue;
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
    public void addTask(DownloadController controller){
        if(controller.isFinish()){
            return;
        }
        mBlockingQueue.add(controller);
    }
    public void cancelTask(DownloadController controller){
        if(controller.isFinish()){
            return;
        }
        mBlockingQueue.remove(controller);
    }

    @Override
    public void run(){
        while(isRunning){
            try {
                for(Iterator<DownloadController> it = mRunningQueue.iterator();it.hasNext();){
                    if(it.next().isFinish()){
                        it.remove();
                        current_downloadTask_count --;
                    }
                }
                if(current_downloadTask_count < MAX_PARALLEL_THREAD_COUNT){
                    if(mBlockingQueue.size() > 0){
                        DownloadController controller = mBlockingQueue.remove(0);
                        sendMessage(controller);
                        mRunningQueue.add(controller);
                        current_downloadTask_count++;
                    }
                }
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void Stop(){
        isRunning = false;
    }
    private void sendMessage(DownloadController controller){
        mHandler.sendMessage(mHandler.obtainMessage(100,controller));
    }
}
