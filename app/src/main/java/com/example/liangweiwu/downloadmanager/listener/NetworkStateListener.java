package com.example.liangweiwu.downloadmanager.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.liangweiwu.downloadmanager.util.NetworkUtils;

/**
 *  Created by Nol
 */
public class NetworkStateListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //this.stop();
        boolean isConnected = NetworkUtils.getInstance().isNetworkAvailable();
        if(isConnected){
            //this.restart();
        }else{
            Toast.makeText(context, "网络中断，请检查网络连接！",Toast.LENGTH_SHORT).show();
        }
    }
}
