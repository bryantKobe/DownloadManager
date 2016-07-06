package com.example.liangweiwu.downloadmanager.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.example.liangweiwu.downloadmanager.Helper.ApkInfoAccessor;
import com.example.liangweiwu.downloadmanager.Helper.DownloadItemAdapter;
import com.example.liangweiwu.downloadmanager.Model.DownloadController;
import com.example.liangweiwu.downloadmanager.Model.DownloadParam;
import com.example.liangweiwu.downloadmanager.Model.DownloadTask;
import com.example.liangweiwu.downloadmanager.Model.GameInformation;
import com.example.liangweiwu.downloadmanager.Services.FloatingService;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.Utils.FileUtils;
import com.example.liangweiwu.downloadmanager.Utils.GameInformationUtils;
import com.example.liangweiwu.downloadmanager.Utils.GameParamUtils;
import com.example.liangweiwu.downloadmanager.Utils.NetworkUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivityTest";

    private RecyclerView.Adapter mAdapter;
    private ArrayList<DownloadController> mTask = new ArrayList<>();
    private ArrayList<DownloadItemAdapter.UpdateParams> mUpdateParams = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        onLaunch();
    }
    private void onLaunch(){
        GameInformationUtils.init(this);
        GameParamUtils.init(this);
        FileUtils.init(this);
        NetworkUtils.init(this);
        ApkInfoAccessor.init(this);
        dataInit();
        uiInit();
    }
    private void uiInit(){
        //startService(new Intent(MainActivity.this, FloatingService.class));
    }
    private void dataInit(){
        String url = "http://mydata.xxzhushou.cn/web_server/upload/app/2016-03-04/com.DBGame.DiabloLOL.apk";
        int thread_num = 5;
        GameInformationUtils.getInstance().clear();
        GameInformation info = GameInformationUtils.getInstance().createGameInfo(url,thread_num);
        //GameInformationUtils.getInstance().debug();


        ArrayList<GameInformation> info_list = GameInformationUtils.getInstance().getGameList();
        HashMap<Integer,DownloadParam[]> params_map = GameParamUtils.getInstance().getParamMap();
        for(GameInformation info_temp : info_list){
            final DownloadItemAdapter.UpdateParams pp = new DownloadItemAdapter.UpdateParams();
            DownloadController task = new DownloadController(info_temp,params_map.get(info_temp.getID())){
                @Override
                public void initViews(Integer... values) {
                    pp.updateParams(values);
                    mAdapter.notifyDataSetChanged();
                }
                @Override
                public void bindViews(Integer... values) {
                    pp.updateParams(values);
                    mAdapter.notifyDataSetChanged();
                }
                @Override
                public void onDownloadStop() {
                    System.out.println("stop");
                    pp.setFinished();
                    mAdapter.notifyDataSetChanged();
                }
            };
            pp.setController(task);
            mUpdateParams.add(pp);
            //task.start();
            mTask.add(task);
        }
        mAdapter = new DownloadItemAdapter(mUpdateParams);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.downloadList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        checkAnimInit();
    }
    private void checkAnimInit() {
        final Animation animation = new RotateAnimation(0, -89, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setDuration(200);

        final ImageView iv = (ImageView) findViewById(R.id.checkImageView);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: animator success");
                iv.startAnimation(animation);
            }
        });
    }
    @Override
    protected void onStop(){
        System.out.println("stop");
        super.onStop();
        GameInformationUtils.getInstance().onDestroy();
        GameParamUtils.getInstance().onDestroy();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


    private void netTest(){
        System.out.println("network:" + NetworkUtils.getInstance().isNetworkAvailable());
        System.out.println("wifi:" + NetworkUtils.getInstance().isWifi());
        System.out.println("3G:" + NetworkUtils.getInstance().is3G());
    }
    private void downloadTest(){
        String url = "http://mydata.xxzhushou.cn/web_server/upload/app/2016-03-04/com.DBGame.DiabloLOL.apk";
        int thread_number = 5;
        try{
            final DownloadController task = new DownloadController(url,thread_number){
                @Override
                public void bindViews(Integer... values){
                    Log.d("progress","downloaded size:" + values[0] + " b; speed: " + values[1] + " b/s");
                }
                @Override
                public void initViews(Integer... values){
                    Log.d("progress","init");
                    Log.d("fileSize",values[0] + " b");
                    Log.d("downloadedSize",values[1] + " b");
                }
                @Override
                public void onDownloadStop(){
                    debug();
                    Log.d("progress","stop");
                }
            };
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

}

