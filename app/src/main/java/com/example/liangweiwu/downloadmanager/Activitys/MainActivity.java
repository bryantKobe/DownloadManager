package com.example.liangweiwu.downloadmanager.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.liangweiwu.downloadmanager.Model.DownloadController;
import com.example.liangweiwu.downloadmanager.Services.FloatingService;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.Utils.FileUtils;
import com.example.liangweiwu.downloadmanager.Utils.GameInformationUtils;
import com.example.liangweiwu.downloadmanager.Utils.GameParamUtils;
import com.example.liangweiwu.downloadmanager.Utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mFloatingBtn = (Button) findViewById(R.id.floating_btn);
        mFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FloatingService.class);
                startService(intent);
                finish();
            }
        });
        onLaunch();
        netTest();
        downloadTest();
    }
    private void onLaunch(){
        GameInformationUtils.init(this);
        GameParamUtils.init(this);
        FileUtils.init(this);
        NetworkUtils.init(this);
    }
    private void netTest(){
        System.out.println("network:" + NetworkUtils.getInstance().isNetworkAvailable());
        System.out.println("wifi:" + NetworkUtils.getInstance().isWifi());
        System.out.println("3G:" + NetworkUtils.getInstance().is3G());
    }
    private void downloadTest(){
        String url = "http://down1.xxzhushou.cn/uploads/2016-04-08/com.youyou.hylt.guopan-1.0_s_1460103121.apk";
        int thread_number = 5;
        try{
            final DownloadController task = new DownloadController(url,thread_number);
            Button start = (Button)findViewById(R.id.thread_start);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    task.start();
                }
            });
            Button pause = (Button)findViewById(R.id.thread_pause);
            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    task.pause();
                }
            });
            Button resume = (Button)findViewById(R.id.thread_resume);
            resume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    task.resume();
                }
            });
            Button stop = (Button)findViewById(R.id.thread_stop);
            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    task.stop();
                }
            });
            Button restart = (Button)findViewById(R.id.thread_restart);
            restart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    task.restart();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        GameInformationUtils.getInstance().onDestroy();
        GameParamUtils.getInstance().onDestroy();
    }
}
