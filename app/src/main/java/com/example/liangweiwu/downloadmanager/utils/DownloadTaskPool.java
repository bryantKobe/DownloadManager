package com.example.liangweiwu.downloadmanager.utils;

import android.os.Handler;

import com.example.liangweiwu.downloadmanager.model.ApkInformation;
import com.example.liangweiwu.downloadmanager.model.DownloadTaskController;
import com.example.liangweiwu.downloadmanager.utils.FileUtils;
import com.example.liangweiwu.downloadmanager.utils.GameInformationUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadTaskPool extends Thread{
    public static final int MAX_PARALLEL_THREAD_COUNT = 2;
    public static final int TASK_PRIORITY_NORMAL = 0;
    public static final int TASK_PRIORITY_HIGHEST = 1;

    private static ExecutorService exec = Executors.newFixedThreadPool(MAX_PARALLEL_THREAD_COUNT);
    private ArrayList<DownloadTaskController> mBlockingQueue;
    private ArrayList<DownloadTaskController> mRunningQueue;
    private ArrayList<DownloadTaskController> mStoppingQueue;
    private ArrayList<DownloadTaskController> mFinishedQueue;
    private Handler mHandler;
    private int current_downloadTask_count;
    private boolean isRunning = true;
    private boolean isBlocked = false;

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
    public void addTask(DownloadTaskController controller, int priority){
        if(controller.isFinish()){
            if(!controller.getInfo().isInstalled()){
                mFinishedQueue.add(controller);
                return;
            }
            return;
        }
        if(priority == TASK_PRIORITY_NORMAL){
            mBlockingQueue.add(controller);
        }else if(priority == TASK_PRIORITY_HIGHEST){
            mBlockingQueue.add(0,controller);
        }
    }
    public void addTask(DownloadTaskController controller){
        addTask(controller,TASK_PRIORITY_NORMAL);
    }
    public void cancelTask(DownloadTaskController controller){
        if(controller.isFinish()){
            return;
        }
        controller.stop();
        mStoppingQueue.add(controller);
        mBlockingQueue.remove(controller);
    }
    public void deleteTask(DownloadTaskController controller){
        cancelTask(controller);
        ApkInformation info = controller.getInfo();
        String fileName = info.getFileName();
        if(FileUtils.deleteApk(fileName)){
            GameInformationUtils.getInstance().delete(info.getID());
            mHandler.sendMessage(mHandler.obtainMessage(400,info.getID()));
        }
    }
    public void onTaskFinish(DownloadTaskController controller){
        mHandler.sendMessage(mHandler.obtainMessage(200,controller));
    }
    public String setApkInstalled(String packageName){
        int id = GameInformationUtils.getInstance().setApkInstalled(packageName);
        String appName = null;
        if(id != ApkInformation.EMPTY_ID){
            for(Iterator<DownloadTaskController> it = mFinishedQueue.iterator(); it.hasNext();) {
                DownloadTaskController controller = it.next();
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
                for(Iterator<DownloadTaskController> it = mRunningQueue.iterator(); it.hasNext();){
                    DownloadTaskController controller = it.next();
                    if(mStoppingQueue.contains(controller) && !controller.isFinish()){
                        it.remove();
                        current_downloadTask_count --;
                        continue;
                    }
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
                //Log.d("blocked","" + mBlockingQueue.size());
                //Log.d("running","" + mRunningQueue.size());
                if(current_downloadTask_count < MAX_PARALLEL_THREAD_COUNT && !isBlocked){
                    if(mBlockingQueue.size() > 0){
                        DownloadTaskController controller = mBlockingQueue.remove(0);
                        mHandler.sendMessage(mHandler.obtainMessage(100,controller));
                        mRunningQueue.add(controller);
                        current_downloadTask_count++;
                    }
                }
                for(Iterator<DownloadTaskController> it = mStoppingQueue.iterator(); it.hasNext();){
                    DownloadTaskController controller = it.next();
                    if(controller.isFinish()){
                        it.remove();
                    }
                }
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void Stop(){
        //isBlocked = true;
        for(DownloadTaskController controller : mRunningQueue){
            //cancelTask(controller);
            //addTask(controller,TASK_PRIORITY_HIGHEST);
        }
        //isBlocked = false;
    }

}
