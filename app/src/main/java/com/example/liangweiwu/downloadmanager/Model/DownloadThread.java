package com.example.liangweiwu.downloadmanager.Model;

import android.os.Handler;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.Model.DownloadParam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class DownloadThread extends Thread {
    /** 文件保存路径 */
    private File file;
    /** 下载参数 */
    private DownloadParam param;
    /** 当前下载是否完成 */
    private boolean isCompleted = false;
    /** 下载数据是否成功写入 */
    private boolean isSuccessful = false;
    /** 当前下载文件长度 */
    private int downloadLength = 0;
    /** 线程中断标志 */
    private boolean isPreStop = false;
    private boolean isPostStop = false;
    private boolean forcedStop = false;

    /**
     *  url:文件下载地址
     *  file:文件保存路径
     *  blockSize:下载数据长度
     *  threadId:线程ID
     *  startOffset:开始位置偏移量
     */
    public DownloadThread(DownloadParam param,File file){
        this.file = file;
        this.param = param;
    }
    @Override
    public void run() {
        if(param.getThread_status()==DownloadParam.THREAD_STATUS_COMPLETED){
            isSuccessful = true;
            isCompleted = true;
            isPreStop = true;
            isPostStop = true;
            Log.d("Thread "+param.getThread_id(), "Completed");
            return;
        }
        BufferedInputStream bis = null;
        RandomAccessFile raf = null;
        int blockSize = param.getThread_blockSize();
        int threadId = param.getThread_id();
        int startOffset = param.getThread_downloadedLength();
        int startPos = blockSize * threadId + startOffset;//开始位置
        int endPos = blockSize * (threadId + 1) - 1;//结束位置
        byte[] buffer = new byte[1024];
        boolean isSuccessfulTemp;
        while(!forcedStop && !isSuccessful){
            isSuccessfulTemp = true;        //数据写入失败，自动重启线程
            downloadLength = 0;         //重置已经下载数据量
            try {
                URL downloadUrl = new URL(param.getUrl());              //MalformedURLException
                URLConnection conn = downloadUrl.openConnection();      //IOException
                conn.setAllowUserInteraction(true);
                //设置当前线程下载的起点、终点
                conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                Log.d("download",Thread.currentThread().getName() + "  bytes=" + startPos + "-" + endPos);
                bis = new BufferedInputStream(conn.getInputStream());   //IOException
                raf = new RandomAccessFile(file, "rwd");                //FileNotFoundException
                raf.seek(startPos);
                int len;
                while (((len = bis.read(buffer, 0, 1024)) != -1) && !isInterrupted()) {
                    raf.write(buffer, 0, len);
                    downloadLength += len;
                }
                if(!isInterrupted()){
                    isCompleted = true;
                }
                isPreStop = true;
            }catch (InterruptedIOException e){

            }catch (IOException e1){
                isSuccessfulTemp = false;
                e1.printStackTrace();
            }catch (Exception e2){
                e2.printStackTrace();
            }finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        isSuccessfulTemp = false;
                        e.printStackTrace();
                    }
                }
                isSuccessful = isSuccessfulTemp;
                if(isCompleted && isSuccessful){
                    param.setThreadStatus(DownloadParam.THREAD_STATUS_COMPLETED);
                    Log.d("Thread:"+param.getThread_id(), "Finished,all size:" + downloadLength);
                }
                isPostStop = true;
            }
        }
    }
    /**
     *
     */
    public void Stop(){
        forcedStop = true;
        if(isPreStop){
            return;
        }
        interrupt();
        while(!isPostStop){
            //Log.d("download","Stopping Thread "+ threadId);
        }
        Log.d("download","Thread:" + param.getThread_id() + " has stopped,downloaded size:" + downloadLength);
        param.update(downloadLength);
    }
    /**
     * 线程文件是否下载完毕
     */
    public boolean isCompleted() {
        return isCompleted;
    }
    public boolean isEnd(){
        return isPostStop||isPreStop;
    }
    /**
     * 下载数据是否成功写入
     */
    public boolean isSuccessful(){
        return isSuccessful;
    }
    /**
     * 线程下载文件长度
     */
    public int getDownloadLength() {
        return downloadLength;
    }
}
