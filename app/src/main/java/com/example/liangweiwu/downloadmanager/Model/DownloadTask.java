package com.example.liangweiwu.downloadmanager.Model;

import android.os.AsyncTask;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.Model.DownloadParam;
import com.example.liangweiwu.downloadmanager.Model.DownloadThread;
import com.example.liangweiwu.downloadmanager.Model.GameInformation;
import com.example.liangweiwu.downloadmanager.Utils.FileUtils;
import com.example.liangweiwu.downloadmanager.Utils.GameInformationUtils;
import com.example.liangweiwu.downloadmanager.Utils.GameParamUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;


public class DownloadTask extends AsyncTask<Integer,Integer,String> {
    public static final int DOWNLOAD_STATE_NEW = 0;           //下载状态：新线程
    public static final int DOWNLOAD_STATE_RUNNABLE = 1;      //下载状态：准备运行
    public static final int DOWNLOAD_STATE_RUNNING = 2;       //下载状态：运行中
    public static final int DOWNLOAD_STATE_PAUSED = 3;        //下载状态：暂停
    public static final int DOWNLOAD_STATE_TERMINATED = 4;    //下载状态：终止
    public static final int DOWNLOAD_STATE_END = 5;           //下载状态：结束
    public static final int DOWNLOAD_STATE_FAILED = 6;        //下载状态：失败

    private URL url;
    private int threadNum;                  // 开启的线程数
    private String filePath;                // 保存文件路径地址
    private File file;
    private int blockSize;                  // 每一个线程的下载量
    private int fileSize;                   // 下载文件的大小
    private int downloadedSize = 0;      //已下载的文件大小
    private DownloadThread[] threads;       //线程池
    private DownloadParam[] params;         //参数池
    private GameInformation info;
    private int download_states = DOWNLOAD_STATE_NEW;

    /**
     **  继续未完成的下载任务
     **/
    public DownloadTask(GameInformation info,DownloadParam[] params) throws Exception{
        this.params = params;
        this.info = info;
        init((String)info.getAttribution("url"),(Integer)info.getAttribution("thread_number"));
        for(int i = 0 ; i < threadNum ; i++){
            threads[i] = new DownloadThread(params[i], file);
            threads[i].setName("Thread:" + i);
        }
    }
    /**
     **  新建下载任务
     **/
    public DownloadTask(String downloadUrl, int threadNum) throws Exception{
        info = GameInformationUtils.getInstance().createGameInfo();
        info.setAttribute("url",downloadUrl);
        info.setAttribute("thread_number",threadNum);
        info.setAttribute("package",DownloadParam.getFilename(downloadUrl));
        info.setAttribute("status",0);
        init(downloadUrl,threadNum);
        params = new DownloadParam[threadNum];
        for(int i = 0 ; i < threadNum; i++){
            params[i] = new DownloadParam(info.getID(),(String)info.getAttribution("url"),i,0);
            threads[i] = new DownloadThread(params[i], file);
            threads[i].setName("Thread:" + i);
        }
    }
    private void init(String downloadUrl, int threadNum) throws Exception{
        this.threadNum = threadNum;
        file = new File(FileUtils.DIR_PACKAGE + info.getAttribution("package"));
        this.threads = new DownloadThread[threadNum];
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
    }

    //doInBackground执行完后由UI线程调用，用于更新界面操作
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    //在PreExcute执行后被启动AysncTask的后台线程调用，将结果返回给UI线程
    @Override
    protected String doInBackground(Integer... args){
        if(info.getAttribution("status") == 1){
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
            while(download_states != DOWNLOAD_STATE_TERMINATED && download_states != DOWNLOAD_STATE_END){
                while(download_states != DOWNLOAD_STATE_TERMINATED && download_states != DOWNLOAD_STATE_RUNNABLE){
                    Log.d("download","waiting");
                    Thread.sleep(1000);
                }
                if(download_states == DOWNLOAD_STATE_TERMINATED){
                    break;
                }
                Log.d("download","running");
                download_states = DOWNLOAD_STATE_RUNNING;
                for (int i = 0; i < threadNum; i++) {
                    if(threads[i].isEnd()){
                        threads[i] = new DownloadThread(params[i],file);
                        threads[i].setName("Thread:" + i);
                    }
                    threads[i].start();
                }
                boolean isFinished = false;
                int speedPerSecond = 0;
                int preDownloadedSize = downloadedSize;
                int downloadedAllSize = downloadedSize;
                while (!isFinished) {
                    isFinished = true;
                    downloadedAllSize = downloadedSize;
                    for (int i = 0; i < threadNum; i++) {
                        downloadedAllSize += threads[i].getDownloadLength();
                        if (!(threads[i].isEnd() || threads[i].isCompleted() || threads[i].isSuccessful())) {
                            isFinished = false;
                        }
                    }
                    speedPerSecond = downloadedAllSize - preDownloadedSize;
                    preDownloadedSize = downloadedAllSize;
                    publishProgress(downloadedAllSize,speedPerSecond);
                    Thread.sleep(1000);
                }
                if(downloadedAllSize == fileSize){
                    // TODO
                    //
                    // 判断文件是否写入

                    download_states = DOWNLOAD_STATE_END;
                    info.setAttribute("status",1);

                    // TODO
                    //
                    // 获取apk的name、icon、versionCode、versionName、category、detail
                }
                Log.d("download", " all of downloadSize:" + downloadedAllSize);
            }
        }catch (Exception e) {
            //IOException、InterruptedIOException
            download_states = DOWNLOAD_STATE_FAILED;
            e.printStackTrace();
            Log.e("download","download failed!");
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.d("download", values[0] + " b");
        super.onProgressUpdate(values);
    }
    public void Start(){
        if(download_states != DOWNLOAD_STATE_NEW){
            return;
        }
        download_states = DOWNLOAD_STATE_RUNNABLE;
        execute();
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
        GameParamUtils.getInstance().saveParams(params);
    }
    public int getID(){
        return info.getID();
    }
    public GameInformation getInfo(){
        return info;
    }
    public DownloadParam[] getParams(){
        return params;
    }
    public int getDownloadState(){
        return download_states;
    }
}
