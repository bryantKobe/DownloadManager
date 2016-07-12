package com.example.liangweiwu.downloadmanager.thread;

import com.example.liangweiwu.downloadmanager.view.event.MainUiEvent;
import com.example.liangweiwu.downloadmanager.model.ApkInformation;
import com.example.liangweiwu.downloadmanager.model.DownloadTaskController;
import com.example.liangweiwu.downloadmanager.util.FileUtils;
import com.example.liangweiwu.downloadmanager.util.ApkInfoUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;

public class DownloadTaskPoolThread extends Thread{
    public static final int MAX_PARALLEL_THREAD_COUNT = 2;
    public static final int TASK_PRIORITY_NORMAL = 0;
    public static final int TASK_PRIORITY_HIGHEST = 1;

    private static DownloadTaskPoolThread mDownloadTaskPool;
    private ExecutorService exec;
    private ArrayList<DownloadTaskController> mBlockingQueue;
    private ArrayList<DownloadTaskController> mRunningQueue;
    private ArrayList<DownloadTaskController> mStoppingQueue;
    private ArrayList<DownloadTaskController> mFinishedQueue;
    private int current_downloadTask_count;
    private boolean isRunning = true;
    private boolean isBlocked = false;

    public static DownloadTaskPoolThread getInstance(){
        if(mDownloadTaskPool == null){
            mDownloadTaskPool = new DownloadTaskPoolThread();
        }
        return mDownloadTaskPool;
    }

    public DownloadTaskPoolThread(){
        exec = Executors.newFixedThreadPool(MAX_PARALLEL_THREAD_COUNT);
        mBlockingQueue = new ArrayList<>();
        mRunningQueue = new ArrayList<>();
        mStoppingQueue = new ArrayList<>();
        mFinishedQueue = new ArrayList<>();
        current_downloadTask_count = 0;
    }
    public void executeTask(DownloadMainThread task){
        task.executeOnExecutor(exec);
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
        final ApkInformation info = controller.getInfo();
        ApkInfoUtils.getInstance().delete(info.getID());
        MainUiEvent event = new MainUiEvent(MainUiEvent.EVENT_TASK_DELETE,info.getID());
        postEvent(event);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String fileName = info.getFileName();
                FileUtils.deleteApk(fileName);
            }
        }).start();
    }
    public void onTaskFinish(DownloadTaskController controller){
        MainUiEvent event = new MainUiEvent(MainUiEvent.EVENT_TASK_UPDATE_FLOAT_ICON,controller);
        postEvent(event);
    }
    public String setApkInstalled(String packageName){
        int id = ApkInfoUtils.getInstance().setApkInstalled(packageName);
        String appName = null;
        if(id != ApkInformation.EMPTY_ID){
            for(Iterator<DownloadTaskController> it = mFinishedQueue.iterator(); it.hasNext();) {
                DownloadTaskController controller = it.next();
                if (controller.getInfo().getID() == id) {
                    appName = controller.getInfo().getName();
                    controller.setApkInstall();
                    MainUiEvent event = new MainUiEvent(MainUiEvent.EVENT_TASK_UPDATE);
                    postEvent(event);
                    it.remove();
                    break;
                }
            }
        }
        return appName;
    }
    private void postEvent(MainUiEvent event){
        EventBus.getDefault().post(event);
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
                        MainUiEvent event = new MainUiEvent(MainUiEvent.EVENT_TASK_START,controller);
                        postEvent(event);
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
                Thread.sleep(500);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void onActivityDestroy(){
        isBlocked = true;
        for(DownloadTaskController controller : mRunningQueue){
            cancelTask(controller);
            addTask(controller,TASK_PRIORITY_HIGHEST);
        }
        mRunningQueue.clear();
        current_downloadTask_count = 0;
        isBlocked = false;
    }

}
