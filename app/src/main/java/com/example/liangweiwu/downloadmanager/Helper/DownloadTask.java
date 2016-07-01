package com.example.liangweiwu.downloadmanager.Helper;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.liangweiwu.downloadmanager.Model.DownloadParam;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class DownloadTask extends AsyncTask<DownloadParam,Integer,String> {
    private View view;
    private String downloadUrl;// 下载链接地址
    private int threadNum;// 开启的线程数
    private String filePath;// 保存文件路径地址
    private int blockSize;// 每一个线程的下载量

    public DownloadTask(View view){
        this.view = view;
    }
    public DownloadTask(View view,String downloadUrl, int threadNum, String filepath) {
        this.downloadUrl = downloadUrl;
        this.threadNum = threadNum;
        this.filePath = filepath;
    }

    //DownloadTask被后台线程执行后，被UI线程被调用，一般用于初始化界面控件，如进度条
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    //doInBackground执行完后由UI线程调用，用于更新界面操作
    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub


        super.onPostExecute(result);
    }

    //在PreExcute执行后被启动AysncTask的后台线程调用，将结果返回给UI线程
    @Override
    protected String doInBackground(DownloadParam... params) {
        // TODO Auto-generated method stub
        DownloadThread[] threads = new DownloadThread[threadNum];
        try {
            URL url = new URL(downloadUrl);
            Log.d("download", "download file http path:" + downloadUrl);
            URLConnection conn = url.openConnection();
            // 读取下载文件总大小
            int fileSize = conn.getContentLength();
            if (fileSize <= 0) {
                System.out.println("读取文件失败");
                // exit
                return null;
            }

            // 计算每条线程下载的数据长度
            blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum : fileSize / threadNum + 1;

            Log.d("download", "fileSize:" + fileSize + "  blockSize:");

            File file = new File(filePath);
            for (int i = 0; i < threads.length; i++) {
                // 启动线程，分别下载每个线程需要下载的部分
                threads[i] = new DownloadThread(url, file, blockSize, (i + 1),0);
                threads[i].setName("Thread:" + i);
                threads[i].start();
            }

            boolean isfinished = false;
            int downloadedAllSize = 0;
            while (!isfinished) {
                isfinished = true;
                // 当前所有线程下载总量
                downloadedAllSize = 0;
                for (int i = 0; i < threads.length; i++) {
                    downloadedAllSize += threads[i].getDownloadLength();
                    if (!threads[i].isCompleted()) {
                        isfinished = false;
                    }
                }
                publishProgress(downloadedAllSize);
                Thread.sleep(1000);// 休息1秒后再读取下载进度
            }
            Log.d("download", " all of downloadSize:" + downloadedAllSize);

        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        Log.d("download", String.valueOf(values[0])+" b");
        super.onProgressUpdate(values);
    }
}
