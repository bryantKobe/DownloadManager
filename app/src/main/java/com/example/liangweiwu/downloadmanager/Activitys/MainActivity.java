package com.example.liangweiwu.downloadmanager.Activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.example.liangweiwu.downloadmanager.Helper.ApkInfoAccessor;
import com.example.liangweiwu.downloadmanager.Helper.DownloadItemAdapter;
import com.example.liangweiwu.downloadmanager.Helper.UrlChecker;
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
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivityTest";
    public static final int MAX_PARALLEL_THREAD_COUNT = 2;
    public static final int DEFAULT_THREAD_COUNT = 5;
    public static ExecutorService exec = Executors.newFixedThreadPool(MAX_PARALLEL_THREAD_COUNT);


    private RecyclerView.Adapter mAdapter;
    private ArrayList<DownloadItemAdapter.UpdateParams> mUpdateParams = new ArrayList<>();
    private UrlCheckHandler mHandler;


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
        startService(new Intent(MainActivity.this, FloatingService.class));
    }
    private void dataInit(){
        mHandler = new UrlCheckHandler(this);
        //String url1 = "http://mydata.xxzhushou.cn/web_server/upload/app/2016-03-04/com.DBGame.DiabloLOL.apk";
        //String url2 = "http://mydata.xxzhushou.cn/web_server/upload/app/2016-05-10/Super_Cat_v1.101x.apk";
        //int thread_num = 5;
        //GameInformationUtils.getInstance().clear();
        //GameInformation info1 = GameInformationUtils.getInstance().createGameInfo(url1,thread_num);
        //GameInformation info2 = GameInformationUtils.getInstance().createGameInfo(url2,thread_num);
        //GameInformationUtils.getInstance().debug();
        ((TextView)findViewById(R.id.url_edit)).setText("http://down1.xxzhushou.cn/uploads/2016-04-08/com.youyou.hylt.guopan-1.0_s_1460103121.apk");

        ArrayList<GameInformation> info_list = GameInformationUtils.getInstance().getGameList();
        HashMap<Integer,DownloadParam[]> params_map = GameParamUtils.getInstance().getParamMap();
        for(GameInformation info_temp : info_list){
            final DownloadItemAdapter.UpdateParams pp = DownloadController.createInstance(info_temp,params_map.get(info_temp.getID()),mAdapter);
            mUpdateParams.add(pp);
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
        final TextView tv = (TextView) findViewById(R.id.url_edit);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv.startAnimation(animation);
                new UrlChecker(mHandler,tv.getText().toString()).start();
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

    public class UrlCheckHandler extends Handler {
        Context mContext;
        Toast toast;

        public UrlCheckHandler(Context mContext) {
            this.mContext = mContext;
            toast = Toast.makeText(mContext, R.string.null_str,Toast.LENGTH_SHORT);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UrlChecker.URL_VALID:
                    toast.setText("即将开始下载..." + msg.obj);
                    toast.show();
                    //TODO:add download task to list
                    String url = (String) msg.obj;
                    DownloadItemAdapter.UpdateParams pp = DownloadController.createInstance(url,DEFAULT_THREAD_COUNT,mAdapter);
                    mUpdateParams.add(pp);
                    mAdapter.notifyDataSetChanged();
                    break;
                case UrlChecker.URL_INVALID:
                    toast.setText("URL非法或已存在下载记录");
                    toast.show();
            }
        }
    }

}

