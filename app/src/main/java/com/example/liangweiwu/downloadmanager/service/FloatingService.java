package com.example.liangweiwu.downloadmanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.example.liangweiwu.downloadmanager.util.FloatingWindowManager;

import java.util.Timer;

public class FloatingService extends Service {
    private Handler mHandler = new Handler();
    private Timer mTimer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FloatingWindowManager.createFloatingIcon(getApplicationContext());
        // 开启定时器，每隔0.5秒刷新一次
        if (mTimer == null) {
            mTimer  = new Timer();
            //mTimer .scheduleAtFixedRate(new RefreshTask(), 0, 500);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Service被终止的同时也停止定时器继续运行
        mTimer.cancel();
        mTimer  = null;
    }
}
