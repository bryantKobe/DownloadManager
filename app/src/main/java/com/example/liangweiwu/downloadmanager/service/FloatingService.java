package com.example.liangweiwu.downloadmanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.example.liangweiwu.downloadmanager.util.FloatingWindowManager;

import java.util.Timer;
/**
 *  Created by Nol
 */
public class FloatingService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FloatingWindowManager.createFloatingIcon(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Service被终止的同时也停止定时器继续运行
    }
}
