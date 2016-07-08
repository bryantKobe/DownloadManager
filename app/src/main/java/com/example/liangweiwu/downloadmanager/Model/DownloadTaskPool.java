package com.example.liangweiwu.downloadmanager.model;

import android.os.Handler;

import com.example.liangweiwu.downloadmanager.utils.GameInformationUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadTaskPool extends Thread{
    public static final int MAX_PARALLEL_THREAD_COUNT = 2;

    private static ExecutorService exec = Executors.newFixedThreadPool(MAX_PARALLEL_THREAD_COUNT);
    private ArrayList<DownloadController> mBlockingQueue;
    private ArrayList<DownloadController> mRunningQueue;
    private ArrayList<DownloadController> mStoppingQueue;
    private ArrayList<DownloadController> mFinishedQueue;
    private Handler mHandler;
    private int current_downloadTask_count;
    private boolean isRunning = true;

    public static ExecutorService getExec(){
        return exec;
    }
    public DownloadTaskPool(Handler handler){
        mBlockingQueue = new ArrayList<>();
        mRunningQueue = new ArrayList<>();
        mStoppingQueue = new ArrayList<>();
        mFinishedQueue = new ArrayList<>();
        this.mHandler = handler;
        current_downloadTask_count = 0;
    }
    public void addTask(DownloadController controller){
        if(controller.isFinish()){
            if(!controller.getInfo().isInstalled()){
                mFinishedQueue.add(controller);
                return;
            }
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
    public void onTaskFinish(DownloadController controller){
        mHandler.sendMessage(mHandler.obtainMessage(200,controller));
    }
    public String setApkInstalled(String packageName){
        int id = GameInformationUtils.getInstance().setApkInstalled(packageName);
        String appName = null;
        if(id != GameInformation.EMPTY_ID){
            for(Iterator<DownloadController> it = mFinishedQueue.iterator();it.hasNext();) {
                DownloadController controller = it.next();
                if (controller.getInfo().getID() == id) {
                    appName = controller.getInfo().getName();
                    controller.setApkInstall();
                    mHandler.sendMessage(mHandler.obtainMessage(300));
                    it.remove();
                    break;
                }
            }
        }
        return appName;
    }

    @Override
    public void run(){
        while(isRunning){
            try {
                for(Iterator<DownloadController> it = mRunningQueue.iterator();it.hasNext();){
                    DownloadController controller = it.next();
                    if(controller.isFinish()){
                        if(controller.getInfo().isDownloaded()
                                && !controller.getInfo().isInstalled()){
                            mFinishedQueue.add(controller);
                        }
                        onTaskFinish(controller);
                        it.remove();
                        current_downloadTask_count --;
                    }
                }

                if(current_downloadTask_count < MAX_PARALLEL_THREAD_COUNT){
                    if(mBlockingQueue.size() > 0){
                        DownloadController controller = mBlockingQueue.remove(0);
                        mHandler.sendMessage(mHandler.obtainMessage(100,controller));
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
        for(DownloadController controller : mRunningQueue){
            controller.stop();
        }
        isRunning = false;
    }

}
