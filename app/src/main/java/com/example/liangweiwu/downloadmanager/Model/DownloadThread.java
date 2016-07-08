package com.example.liangweiwu.downloadmanager.model;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;


public class DownloadThread extends Thread {
    public static int MAX_LOOP_TIMES = 2;

    public static final int THREAD_STATE_NEW = 0;                   //线程状态:新建
    public static final int THREAD_STATE_RUNNABLE = 1;              //线程状态:可运行
    public static final int THREAD_STATE_RUNNING = 2;               //线程状态:运行中
    public static final int THREAD_STATE_INTERRUPTED = 3;           //线程状态:中断
    public static final int THREAD_STATE_FAILED = 4;                //线程状态:失败
    public static final int THREAD_STATE_COMPLETED = 5;             //线程状态:完成
    public static final int THREAD_STATE_END = 6;                   //线程状态:结束

    /** 文件保存路径 */
    private File file;
    /** 下载参数 */
    private DownloadParam param;
    /** 下载参数 */
    private String url;
    /** 当前下载文件长度 */
    private int downloadLength = 0;
    /** 总下载长度 */
    private int total_downloadLength = 0;
    /** 线程状态变量 */
    private int thread_state = THREAD_STATE_NEW;
    private boolean isInterrupted = false;
    private boolean isCompleted = false;
    private boolean isFailed = false;

    /**
     *  url:文件下载地址
     *  file:文件保存路径
     *  blockSize:下载数据长度
     *  threadId:线程ID
     *  startOffset:开始位置偏移量
     */
    public DownloadThread(DownloadParam param,File file,String downloadUrl){
        this.file = file;
        this.param = param;
        this.url = downloadUrl;
        total_downloadLength = param.getThread_downloadedLength();
    }
    @Override
    public void run() {
        if(param.isCompleted()){
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
        int loopTimes = 0;
        thread_state = THREAD_STATE_RUNNABLE;
        downloadLength = 0;         //重置已经下载数据量
        while(thread_state != THREAD_STATE_END){
            loopTimes ++;
            try {
                if(loopTimes > MAX_LOOP_TIMES){
                    isFailed = true;
                    thread_state = THREAD_STATE_END;
                    break;
                }
                thread_state = THREAD_STATE_RUNNING;
                URL downloadUrl = new URL(url);              //MalformedURLException
                URLConnection conn = downloadUrl.openConnection();      //IOException
                conn.setAllowUserInteraction(true);
                //设置当前线程下载的起点、终点
                int curStartPos = startPos + downloadLength;
                conn.setRequestProperty("Range", "bytes=" + curStartPos + "-" + endPos);
                //Log.d("download",Thread.currentThread().getName() + "  bytes=" + curStartPos + "-" + endPos);
                bis = new BufferedInputStream(conn.getInputStream());   //IOException
                raf = new RandomAccessFile(file, "rwd");                //FileNotFoundException
                raf.seek(curStartPos);
                int len;
                while (((len = bis.read(buffer, 0, 1024)) != -1) && thread_state != THREAD_STATE_INTERRUPTED) {
                    raf.write(buffer, 0, len);
                    downloadLength += len;
                    save();
                }
                if(thread_state != THREAD_STATE_INTERRUPTED){
                    isCompleted = true;
                    thread_state = THREAD_STATE_COMPLETED;
                }
            }catch (Exception e){
                if(!(e instanceof InterruptedIOException && isInterrupted())){
                    System.out.println("Thread " + threadId + ": failed!");
                    thread_state = THREAD_STATE_FAILED;
                }
                //e.printStackTrace();
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
                        e.printStackTrace();
                        isCompleted = false;
                        downloadLength = 0;                     //写入失败,下载数据清零
                        thread_state = THREAD_STATE_FAILED;
                    }
                }
                if(isCompleted || thread_state == THREAD_STATE_COMPLETED){
                    param.setCompleted();
                    thread_state = THREAD_STATE_END;
                }
                if(isInterrupted || thread_state == THREAD_STATE_INTERRUPTED){
                    thread_state = THREAD_STATE_END;
                }
                if(isFailed){
                    thread_state = THREAD_STATE_END;
                }
            }
        }
        Log.d("Thread:"+param.getThread_id(), "Finished,all size:" + downloadLength);
        param.update(total_downloadLength + downloadLength);
    }
    /**
     *
     */
    public void Stop(){
        if(thread_state != THREAD_STATE_RUNNING && thread_state != THREAD_STATE_RUNNABLE){
            return;
        }
        thread_state = THREAD_STATE_INTERRUPTED;
        isInterrupted = true;
        interrupt();
        while(thread_state != THREAD_STATE_END){
        }
        Log.d("download","Thread:" + param.getThread_id() + " has stopped,downloaded size:" + downloadLength);

    }
    /**
     * 线程文件是否下载完毕
     */
    public boolean isStop(){
        return isCompleted || isInterrupted || isFailed;
    }
    public boolean isFailed(){
        return isFailed;
    }
    /**
     * 线程下载文件长度
     */
    public int getDownloadLength() {
        return downloadLength;
    }
    private void save(){
        param.update(total_downloadLength + downloadLength);
    }
}
