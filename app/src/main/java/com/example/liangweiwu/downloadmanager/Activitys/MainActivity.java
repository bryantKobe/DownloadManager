package com.example.liangweiwu.downloadmanager.activitys;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.liangweiwu.downloadmanager.helper.ApkInfoAccessor;
import com.example.liangweiwu.downloadmanager.helper.DownloadItemAdapter;
import com.example.liangweiwu.downloadmanager.helper.UrlChecker;
import com.example.liangweiwu.downloadmanager.model.DownloadController;
import com.example.liangweiwu.downloadmanager.model.DownloadParam;
import com.example.liangweiwu.downloadmanager.model.DownloadTaskPool;
import com.example.liangweiwu.downloadmanager.model.GameInformation;
import com.example.liangweiwu.downloadmanager.services.FloatingService;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.utils.FileUtils;
import com.example.liangweiwu.downloadmanager.utils.FloatingWindowManager;
import com.example.liangweiwu.downloadmanager.utils.GameInformationUtils;
import com.example.liangweiwu.downloadmanager.utils.GameParamUtils;
import com.example.liangweiwu.downloadmanager.utils.NetworkUtils;
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

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivityTest";
    public static final int DEFAULT_THREAD_COUNT = 5;

    public static DownloadTaskPool mThread_pool;

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
        checkDrawOverlayPermission();
        FileUtils.init(this);
        ApkInfoAccessor.init(this);
        NetworkUtils.init(this);
        GameParamUtils.init(this);
        GameInformationUtils.init(this);
        GameParamUtils.getInstance().onCreate();
        GameInformationUtils.getInstance().onCreate();
        dataInit();
        uiInit();
        startDownloadTask();
    }

    public final static int REQUEST_CODE = 10010;

    @TargetApi(23)
    public void checkDrawOverlayPermission() {
        int api_level = Build.VERSION.SDK_INT;
        if (api_level >= Build.VERSION_CODES.M){
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        }

    }

    @TargetApi(23)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this,"悬浮窗功能未打开,请在app->permission中打开悬浮窗功能",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uiInit(){
        startService(new Intent(MainActivity.this, FloatingService.class));
    }
    private void dataInit(){
        mHandler = new UrlCheckHandler(this);
        mThread_pool = new DownloadTaskPool(mHandler);
        String url1 = "http://mydata.xxzhushou.cn/web_server/upload/app/2016-03-04/com.DBGame.DiabloLOL.apk";
        String url2 = "http://mydata.xxzhushou.cn/web_server/upload/app/2016-05-10/Super_Cat_v1.101x.apk";
        String url3 = "http://mydata.xxzhushou.cn/web_server/upload/app/2016-01-31/com.tencent.tmgp.hse_000000_jh.apk";
        String url4 = "http://cdn.guopan.cn/web_server/upload/app/2016-04-12/com.yaowan.wszzl.guopan.1.0.1.s.apk";
        //int thread_num = 5;
        //GameInformationUtils.getInstance().clear();
        if(GameInformationUtils.getInstance().getGameList().size() == 0){
            GameInformation info1 = GameInformationUtils.getInstance().createGameInfo(url1,DEFAULT_THREAD_COUNT);
            GameInformation info2 = GameInformationUtils.getInstance().createGameInfo(url2,DEFAULT_THREAD_COUNT);
            GameInformation info3 = GameInformationUtils.getInstance().createGameInfo(url3,DEFAULT_THREAD_COUNT);
            GameInformation info4 = GameInformationUtils.getInstance().createGameInfo(url4,DEFAULT_THREAD_COUNT);
        }
        //GameInformationUtils.getInstance().debug();
        //((TextView)findViewById(R.id.url_edit)).setText("http://mydata.xxzhushou.cn/web_server/upload/app/2016-03-04/com.DBGame.DiabloLOL.apk");


        mAdapter = new DownloadItemAdapter(mUpdateParams,mHandler);
        ArrayList<GameInformation> info_list = GameInformationUtils.getInstance().getGameList();
        HashMap<Integer,DownloadParam[]> params_map = GameParamUtils.getInstance().getParamMap();
        for(GameInformation info_temp : info_list){
            final DownloadItemAdapter.UpdateParams pp = DownloadController.createInstance(info_temp,params_map.get(info_temp.getID()),mAdapter);
            mUpdateParams.add(pp);
        }
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
    private void startDownloadTask(){
        mThread_pool.start();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mThread_pool.Stop();
        GameInformationUtils.getInstance().onDestroy();
        GameParamUtils.getInstance().onDestroy();
        Log.d("app","stop");
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
                    int fileSize = msg.arg1;
                    DownloadItemAdapter.UpdateParams pp = DownloadController.createInstance(url,DEFAULT_THREAD_COUNT,mAdapter);
                    pp.getController().getInfo().setAttribute("size",String.valueOf(fileSize));
                    mUpdateParams.add(pp);
                    mThread_pool.addTask(pp.getController());
                    mAdapter.notifyDataSetChanged();
                    ((TextView)findViewById(R.id.url_edit)).setText("");
                    break;
                case UrlChecker.URL_INVALID:
                    toast.setText("URL非法或已存在下载记录");
                    toast.show();
                    break;
                case 100:
                    DownloadController controller = (DownloadController) msg.obj;
                    controller.start();
                    break;
                case 200:
                    Drawable drawable = ((DownloadController) msg.obj).getInfo().getIcon();
                    FloatingWindowManager.updateFloatIcon(drawable);
                    break;
                case 300:
                    mAdapter.notifyDataSetChanged();
                    break;
                case 400:
                    int id = (int) msg.obj;
                    for(DownloadItemAdapter.UpdateParams params : mUpdateParams){
                        if(params.getInfoID() == id){
                            mUpdateParams.remove(params);
                            break;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
        private void insertParams(){
            // TODO
        }
    }

}

