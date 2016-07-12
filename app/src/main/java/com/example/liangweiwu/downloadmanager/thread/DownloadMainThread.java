package com.example.liangweiwu.downloadmanager.thread;

import android.os.AsyncTask;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.model.DownloadParameter;
import com.example.liangweiwu.downloadmanager.model.ApkInformation;
import com.example.liangweiwu.downloadmanager.util.FileUtils;
import com.example.liangweiwu.downloadmanager.util.ApkInfoUtils;
import com.example.liangweiwu.downloadmanager.util.DownloadParameterUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

/**
 *  Created by Nol
 */
public class DownloadMainThread extends AsyncTask<Integer,Integer,String> {
    public static final int DEFAULT_THREAD_COUNT = 5;

    public static final int DOWNLOAD_STATE_NEW = 0;           //下载状态：新线程
    public static final int DOWNLOAD_STATE_RUNNABLE = 1;      //下载状态：准备运行
    public static final int DOWNLOAD_STATE_RUNNING = 2;       //下载状态：运行中
    public static final int DOWNLOAD_STATE_PAUSED = 3;        //下载状态：暂停
    public static final int DOWNLOAD_STATE_TERMINATED = 4;    //下载状态：终止
    public static final int DOWNLOAD_STATE_END = 5;           //下载状态：结束
    public static final int DOWNLOAD_STATE_FAILED = 6;        //下载状态：失败
    public static final int DOWNLOAD_STATE_BLOCKED = 7;       //下载状态:阻塞
    public static final int DOWNLOAD_STATE_INSTALLED = 8;     //下载状态:已安装

    private URL url;
    private int threadNum;                  // 开启的线程数
    private String filePath;                // 保存文件路径地址
    private File file;
    private int blockSize;                  // 每一个线程的下载量
    private int fileSize;                   // 下载文件的大小
    private int downloadedSize = 0;         //已下载的文件大小
    private DownloadSubThread[] threads;       //线程池
    private DownloadParameter[] params;         //参数池
    private ApkInformation info;
    private int download_states = DOWNLOAD_STATE_NEW;


    /**
     **  继续未完成的下载任务
     **/
    public DownloadMainThread(ApkInformation info, DownloadParameter[] params) throws Exception{
        if(params == null){
            params = DownloadParameterUtils.getInstance().createParams(info);
        }
        this.params = params;
        this.info = info;
        String url = info.getUrl();
        init(url,info.getThreadNumber());
        for(int i = 0 ; i < threadNum ; i++){
            threads[i] = new DownloadSubThread(params[i], file, url);
            threads[i].setName("Thread:" + i);
        }
    }
    /**
     **  新建下载任务
     **/
    public DownloadMainThread(String downloadUrl, int threadNum) throws Exception{
        info = ApkInfoUtils.getInstance().createGameInfo(downloadUrl,threadNum);
        info.setAttribute("url",downloadUrl);
        info.setAttribute("thread_number",threadNum);
        params = DownloadParameterUtils.getInstance().createParams(info);
        init(downloadUrl,threadNum);
        for(int i = 0 ; i < threadNum; i++){
            threads[i] = new DownloadSubThread(params[i], file , downloadUrl);
            threads[i].setName("Thread:" + i);
        }
    }
    private void init(String downloadUrl, int threadNum) throws Exception{
        this.threadNum = threadNum;
        this.fileSize = info.getSize();
        file = new File(FileUtils.DIR_PACKAGE + info.getFileName());
        this.threads = new DownloadSubThread[threadNum];
        url = new URL(downloadUrl);
        if(params != null){
            for(int i = 0 ; i < threadNum; i++){
                downloadedSize += params[i].getThread_downloadedLength();
            }
        }
    }
    //DownloadTask被后台线程执行后，被UI线程被调用，一般用于初始化界面控件，如进度条
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        onStart(downloadedSize,0,fileSize);
    }
    //doInBackground执行完后由UI线程调用，用于更新界面操作
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        onStop();
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        onUpdate(values);
    }
    protected void onStart(Integer... values){
    }
    protected void onUpdate(Integer... values){
    }
    protected void onStop(){
    }

    //在PreExcute执行后被启动AysncTask的后台线程调用，将结果返回给UI线程
    @Override
    protected String doInBackground(Integer... args){
        if(info.isDownloaded()){
            download_states = DOWNLOAD_STATE_END;
            return null;
        }
        try {
            URLConnection connection = url.openConnection();                        //IOException
            fileSize = connection.getContentLength();
            if(fileSize <= 0){
                download_states = DOWNLOAD_STATE_FAILED;
                Log.e("download","读取文件失败!");
                return null;
            }
            info.setAttribute("size",String.valueOf(fileSize));
            // 计算每条线程下载的数据长度
            blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum : fileSize / threadNum + 1;
            RandomAccessFile accessFile = new RandomAccessFile(file,"rwd");         //IOException
            accessFile.setLength(fileSize);
            accessFile.close();
            for(int i = 0 ; i < threadNum; i++){
                params[i].setThread_blockSize(blockSize);
            }
            while(download_states != DOWNLOAD_STATE_TERMINATED
                    && download_states != DOWNLOAD_STATE_END
                    && download_states != DOWNLOAD_STATE_FAILED){
                while(download_states != DOWNLOAD_STATE_TERMINATED && download_states != DOWNLOAD_STATE_RUNNABLE){
                    //Log.d("download","waiting");
                    Thread.sleep(1000);
                }
                if(download_states == DOWNLOAD_STATE_TERMINATED){
                    break;
                }
                Log.d("download","running");
                download_states = DOWNLOAD_STATE_RUNNING;
                for (int i = 0; i < threadNum; i++) {
                    if(threads[i].isStop()){
                        threads[i] = new DownloadSubThread(params[i],file,info.getUrl());
                        threads[i].setName("Thread:" + i);
                    }
                    threads[i].start();
                }

                boolean isFailed = false;
                boolean isFinished = false;
                int speedPerSecond = 0;
                int preDownloadedSize = downloadedSize;
                int downloadedAllSize = downloadedSize;
                while (!isFinished) {
                    isFinished = true;
                    downloadedAllSize = downloadedSize;
                    for (int i = 0; i < threadNum; i++) {
                        downloadedAllSize += threads[i].getDownloadLength();
                        if(threads[i].isFailed()){
                            isFailed = true;
                        }
                        if (!threads[i].isStop() || threads[i].isAlive()){
                            isFinished = false;
                        }
                    }
                    if(isFailed){
                        download_states = DOWNLOAD_STATE_FAILED;
                        break;
                    }
                    speedPerSecond = downloadedAllSize - preDownloadedSize;
                    preDownloadedSize = downloadedAllSize;
                    publishProgress(downloadedAllSize,speedPerSecond,fileSize);
                    Thread.sleep(1000);
                }
                if(downloadedAllSize == fileSize){
                    download_states = DOWNLOAD_STATE_END;
                    info.setDownloaded();
                }
                Log.d("download", " all of downloadSize:" + downloadedAllSize);
            }
        }catch (Exception e) {
            //IOException、InterruptedIOException
            download_states = DOWNLOAD_STATE_FAILED;
            e.printStackTrace();
            Log.e("download","download failed!");
        }
        if(download_states == DOWNLOAD_STATE_FAILED){
            onThreadFailed();
        }else if(download_states == DOWNLOAD_STATE_END){
            onDownloadFinished();
        }
        return null;
    }
    private void onThreadFailed(){
        for(int i = 0 ; i < threadNum; i++){
            if(threads[i].isAlive()){
                threads[i].Stop();
            }
        }
    }
    private void onDownloadFinished(){
        ApkInfoUtils.getInstance().onDownloadedFinish(info);
    }
    public void Start(){
        if(download_states != DOWNLOAD_STATE_NEW){
            return;
        }
        download_states = DOWNLOAD_STATE_RUNNABLE;
        DownloadTaskPoolThread.getInstance().executeTask(this);
        Log.d("download","Running");
    }
    public void Pause(){
        if(download_states != DOWNLOAD_STATE_RUNNING
                && download_states != DOWNLOAD_STATE_RUNNABLE){
            return;
        }
        download_states = DOWNLOAD_STATE_PAUSED;
        for(int i = 0 ; i < threads.length; i++){
            if(threads[i].isAlive()){
                threads[i].Stop();
            }
        }
        Log.d("download","Paused");
    }
    public void Resume(){
        if(download_states != DOWNLOAD_STATE_PAUSED){
            return;
        }
        download_states = DOWNLOAD_STATE_RUNNABLE;
        downloadedSize = 0;
        if(params != null){
            for(int i = 0 ; i < threadNum; i++){
                downloadedSize += params[i].getThread_downloadedLength();
            }
        }
        Log.d("download","Resumed");
    }
    public void Stop(){
        if(download_states != DOWNLOAD_STATE_RUNNABLE
                && download_states != DOWNLOAD_STATE_RUNNING
                && download_states != DOWNLOAD_STATE_PAUSED){
            return;
        }
        Pause();
        download_states = DOWNLOAD_STATE_TERMINATED;
        saveParams();
        Log.d("download","Terminated");
    }
    private void saveParams(){
        DownloadParameterUtils.getInstance().saveParams(params);
    }
    public boolean isFinished(){
        return getStatus().equals(Status.FINISHED);
    }
    public ApkInformation getInfo(){
        return info;
    }
    public DownloadParameter[] getParams(){
        return params;
    }
    public int getDownloadState(){
        return download_states;
    }
    public void setApkInstalled(){
        if(download_states != DOWNLOAD_STATE_END){
            return;
        }
        download_states = DOWNLOAD_STATE_INSTALLED;
    }
}
