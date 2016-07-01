package com.example.liangweiwu.downloadmanager.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.liangweiwu.downloadmanager.Helper.DownloadTask;
import com.example.liangweiwu.downloadmanager.Model.DownloadParam;
import com.example.liangweiwu.downloadmanager.Services.FloatingService;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.Utils.DownloadUtils;
import com.example.liangweiwu.downloadmanager.Utils.FileUtils;
import com.example.liangweiwu.downloadmanager.Utils.GameInformationUtils;

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
        downloadTest();
    }
    private void onLaunch(){
        GameInformationUtils.getInstance(this);
        FileUtils.init(this);
        System.out.println(FileUtils.DIR_PACKAGE);
    }
    private void downloadTest(){
        //DownloadUtils.getInstance(this).download();
        String url = "http://down1.xxzhushou.cn/uploads/2016-04-08/com.youyou.hylt.guopan-1.0_s_1460103121.apk";
        DownloadTask task = new DownloadTask(null,url,1,FileUtils.DIR_PACKAGE+"test.apk");
        //DownloadParam param = new DownloadParam("http://www.blogjava.net/toby/archive/2009/04/24/267413.html",0,0);
        //task.execute();
        //System.out.println(param.getFilename());
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        GameInformationUtils.getInstance().onDestory();
    }
}
