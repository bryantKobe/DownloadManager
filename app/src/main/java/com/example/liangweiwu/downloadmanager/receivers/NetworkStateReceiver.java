package com.example.liangweiwu.downloadmanager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.liangweiwu.downloadmanager.utils.NetworkUtils;


public class NetworkStateReceiver extends BroadcastReceiver {
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
