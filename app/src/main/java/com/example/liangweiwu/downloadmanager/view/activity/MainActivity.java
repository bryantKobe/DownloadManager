package com.example.liangweiwu.downloadmanager.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.model.IndexedArrayList;
import com.example.liangweiwu.downloadmanager.proxy.ProxyActivityUtil;
import com.example.liangweiwu.downloadmanager.view.controller.DownloadItemAdapter;
import com.example.liangweiwu.downloadmanager.view.controller.DeleteAnimator;
import com.example.liangweiwu.downloadmanager.view.controller.ViewController;
import com.example.liangweiwu.downloadmanager.view.event.MainUiEvent;
import com.example.liangweiwu.downloadmanager.model.ApkInformation;
import com.example.liangweiwu.downloadmanager.model.DownloadParameter;
import com.example.liangweiwu.downloadmanager.model.DownloadTaskController;
import com.example.liangweiwu.downloadmanager.service.FloatingService;
import com.example.liangweiwu.downloadmanager.thread.DownloadMainThread;
import com.example.liangweiwu.downloadmanager.thread.DownloadTaskPoolThread;
import com.example.liangweiwu.downloadmanager.util.ApkInfoAccessor;
import com.example.liangweiwu.downloadmanager.util.FileUtils;
import com.example.liangweiwu.downloadmanager.util.FloatingWindowManager;
import com.example.liangweiwu.downloadmanager.util.ApkInfoUtils;
import com.example.liangweiwu.downloadmanager.util.DownloadParameterUtils;
import com.example.liangweiwu.downloadmanager.util.NetworkUtils;
import com.example.liangweiwu.downloadmanager.util.UrlChecker;
import java.util.ArrayList;
import java.util.HashMap;
import de.greenrobot.event.EventBus;

/**
 *  Created by Nol & xinxin.li
 */

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_CODE = 10010;
    public static final String TAG = "MainActivityTest";

    private DownloadItemAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private IndexedArrayList<ViewController> mViewController = new IndexedArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        onLaunch();
    }
    private void onLaunch(){
        EventBus.getDefault().register(this);
        FileUtils.init(this);
        ApkInfoAccessor.init(this);
        NetworkUtils.init(this);
        DownloadParameterUtils.init(this);
        ApkInfoUtils.init(this);
        loadData();
    }
    @TargetApi(23)
    public void checkDrawOverlayPermission() {
        int api_level = Build.VERSION.SDK_INT;
        if (api_level >= Build.VERSION_CODES.M){
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }else{
                MainUiEvent.postLaunchEvent();
            }
        }else{
            MainUiEvent.postLaunchEvent();
        }
    }
    @TargetApi(23)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this,"悬浮窗功能未打开,请在app->permission中打开悬浮窗功能",Toast.LENGTH_LONG).show();
            }
            MainUiEvent.postLaunchEvent();
        }
    }
    private void loadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadParameterUtils.getInstance().onCreate();
                ApkInfoUtils.getInstance().onCreate();
                Log.d(TAG, "run: data init");
                EventBus.getDefault().post(new MainUiEvent(MainUiEvent.EVENT_DATA_LOAD_FINISH));
            }
        }).start();
    }
    private void startDownloadTask(){
        if(DownloadTaskPoolThread.getInstance().getState().equals(Thread.State.NEW)){
            DownloadTaskPoolThread.getInstance().start();
        }
    }
    public void onEventMainThread(MainUiEvent event){
        switch (event.what) {
            case MainUiEvent.EVENT_DATA_LOAD_FINISH:
                checkDrawOverlayPermission();
                startService(new Intent(MainActivity.this, FloatingService.class));
                break;
            case MainUiEvent.EVENT_LAUNCH:
                dataInit();
                startDownloadTask();
                break;
            case MainUiEvent.EVENT_URL_VALID:
                Toast.makeText(this, "即将开始下载...", Toast.LENGTH_LONG).show();
                String url = (String) event.obj;
                int fileSize = event.arg1;
                ViewController viewController = DownloadTaskController.createInstance(url, DownloadMainThread.DEFAULT_THREAD_COUNT);
                viewController.getController().getInfo().setAttribute("size", String.valueOf(fileSize));
                mViewController.add(viewController);
                DownloadTaskPoolThread.getInstance().addTask(viewController.getController());
                mAdapter.notifyDataSetChanged();
                ((TextView) findViewById(R.id.url_edit)).setText("");
                break;
            case MainUiEvent.EVENT_URL_INVALID:
                Toast.makeText(this, "URL非法或已存在下载记录", Toast.LENGTH_LONG).show();
                break;
            case MainUiEvent.EVENT_TASK_START:
                DownloadTaskController controller = (DownloadTaskController) event.obj;
                controller.start();
                break;
            case MainUiEvent.EVENT_TASK_UPDATE_FLOAT_ICON:
                Drawable drawable = ((DownloadTaskController) event.obj).getInfo().getIcon();
                FloatingWindowManager.updateFloatIcon(drawable);
                break;
            case MainUiEvent.EVENT_TASK_UPDATE:
                int pos = event.arg2;
                if(pos == MainUiEvent.UPDATE_POSITION_UNDEFINED){
                    mAdapter.notifyDataSetChanged();
                }else{
                    mAdapter.notifyItemChanged(pos);
                }
                break;
            case MainUiEvent.EVENT_TASK_DELETE:
                int id = event.arg1;
                int position = event.arg2;
                mViewController.removeByID(id);
                mAdapter.notifyItemRemoved(position);
                break;
            default:
                break;
        }
    }
    private void dataInit(){
        if(mAdapter != null){
            return;
        }
        String url1 = "http://mydata.xxzhushou.cn/web_server/upload/app/2016-03-04/com.DBGame.DiabloLOL.apk";
        String url2 = "http://mydata.xxzhushou.cn/web_server/upload/app/2016-05-10/Super_Cat_v1.101x.apk";
        String url3 = "http://mydata.xxzhushou.cn/web_server/upload/app/2016-01-31/com.tencent.tmgp.hse_000000_jh.apk";
        if(ApkInfoUtils.getInstance().getGameList().size() == 0){
            ApkInfoUtils.getInstance().createGameInfo(url1, DownloadMainThread.DEFAULT_THREAD_COUNT);
            ApkInfoUtils.getInstance().createGameInfo(url2, DownloadMainThread.DEFAULT_THREAD_COUNT);
            ApkInfoUtils.getInstance().createGameInfo(url3, DownloadMainThread.DEFAULT_THREAD_COUNT);
        }
        mAdapter = new DownloadItemAdapter(mViewController);
        ArrayList<ApkInformation> info_list = ApkInfoUtils.getInstance().getGameList();
        HashMap<Integer,DownloadParameter[]> params_map = DownloadParameterUtils.getInstance().getParamMap();
        for(ApkInformation info_temp : info_list){
            ViewController pp = DownloadTaskController.createInstance(info_temp,params_map.get(info_temp.getID()));
            mViewController.add(pp);
        }
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.downloadList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        DeleteAnimator deleteAnimator = new DeleteAnimator();
        deleteAnimator.setSupportsChangeAnimations(false);
        mRecyclerView.setItemAnimator(deleteAnimator);

        new ItemTouchHelper(mAdapter.getCallback()).attachToRecyclerView(mRecyclerView);
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
                new UrlChecker(tv.getText().toString()).start();
            }
        });

        ImageView qrImage = (ImageView)findViewById(R.id.qrImageView);
        qrImage.setClickable(true);
        qrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProxyActivityUtil.loadProxy(getBaseContext());
                Toast.makeText(getBaseContext(),"targeting to client activity",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        DownloadTaskPoolThread.getInstance().onResume();
    }
    @Override
    protected void onStop(){
        super.onStop();
        ApkInfoUtils.getInstance().onStop();
        DownloadParameterUtils.getInstance().onStop();
        DownloadTaskPoolThread.getInstance().onStop();
        //Log.d(TAG,"stop");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        ApkInfoUtils.getInstance().onDestroy();
        DownloadParameterUtils.getInstance().onDestroy();
        DownloadTaskPoolThread.getInstance().onActivityDestroy();
        EventBus.getDefault().unregister(this);
    }
    private void netTest(){
        Log.d("newtork",""+NetworkUtils.getInstance().isNetworkAvailable());
        Log.d("wifi",""+NetworkUtils.getInstance().isWifi());
        Log.d("3G",""+NetworkUtils.getInstance().is3G());
    }
}

