package com.example.liangweiwu.downloadmanager.Helper;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;


public class DownloadThread extends Thread {
    /** 当前下载是否完成 */
    private boolean isCompleted = false;
    /** 当前下载文件长度 */
    private int downloadLength = 0;
    /** 下载开始位置 */
    private int startOffset = 0;
    /** 文件保存路径 */
    private File file;
    /** 文件下载路径 */
    private URL downloadUrl;
    /** 当前下载线程ID */
    private int threadId;
    /** 线程下载数据长度 */
    private int blockSize;

    /**
     *
     *        sdf:文件下载地址
     * @param file:文件保存路径
     * @param blocksize:下载数据长度
     * @param threadId:线程ID
     */
    public DownloadThread(URL downloadUrl, File file, int blocksize, int threadId,int startOffset) {
        this.downloadUrl = downloadUrl;
        this.file = file;
        this.threadId = threadId;
        this.blockSize = blocksize;
        this.startOffset = startOffset;
    }
    @Override
    public void run() {

        BufferedInputStream bis = null;
        RandomAccessFile raf = null;

        try {
            URLConnection conn = downloadUrl.openConnection();
            conn.setAllowUserInteraction(true);

            int startPos = blockSize * (threadId - 1) + startOffset;//开始位置
            int endPos = blockSize * threadId - 1;//结束位置
            //设置当前线程下载的起点、终点
            conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            Log.d("download",Thread.currentThread().getName() + "  bytes=" + startPos + "-" + endPos);

            byte[] buffer = new byte[1024];
            bis = new BufferedInputStream(conn.getInputStream());

            raf = new RandomAccessFile(file, "rwd");
            raf.seek(startPos);
            int len;
            while ((len = bis.read(buffer, 0, 1024)) != -1) {
                raf.write(buffer, 0, len);
                downloadLength += len;
            }
            isCompleted = true;
            Log.d("download", "current thread task has finished,all size:"
                    + downloadLength);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
                }
            }
        }
    }
    /**
     * 线程文件是否下载完毕
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * 线程下载文件长度
     */
    public int getDownloadLength() {
        return downloadLength;
    }
}
