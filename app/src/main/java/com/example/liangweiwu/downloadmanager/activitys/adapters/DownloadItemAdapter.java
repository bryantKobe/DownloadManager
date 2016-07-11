package com.example.liangweiwu.downloadmanager.activitys.adapters;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liangweiwu.downloadmanager.activitys.MainActivity;
import com.example.liangweiwu.downloadmanager.utils.ApkInfoAccessor;
import com.example.liangweiwu.downloadmanager.model.DownloadTaskController;
import com.example.liangweiwu.downloadmanager.model.thread.DownloadMainThread;
import com.example.liangweiwu.downloadmanager.R;

import java.util.ArrayList;
import java.util.Locale;


public class DownloadItemAdapter extends RecyclerView.Adapter<DownloadItemAdapter.MyViewHolder> {

    private ArrayList<UpdateParams> mDatas;
    private Handler handler = null;

    public DownloadItemAdapter(ArrayList<UpdateParams> mDatas, Handler handler) {
        this.mDatas = mDatas;
        this.handler = handler;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_manager_item_layout, parent, false),this);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final UpdateParams params = mDatas.get(position);
        holder.setParamTag(params);
        int state = params.getController().getDownloadState();
        switch (state){
            case DownloadMainThread.DOWNLOAD_STATE_NEW:
                holder.showWording("等待中","等待下载",Color.BLACK);
                break;
            case DownloadMainThread.DOWNLOAD_STATE_RUNNABLE:
            case DownloadMainThread.DOWNLOAD_STATE_RUNNING:
                holder.updateProgress(params.getDownloadedSize(),params.getSpeed(),params.getFileSize());
                break;
            case DownloadMainThread.DOWNLOAD_STATE_PAUSED:
            case DownloadMainThread.DOWNLOAD_STATE_TERMINATED:
                holder.showWording("继续","已暂停",Color.BLACK);
                break;
            case DownloadMainThread.DOWNLOAD_STATE_FAILED:
                holder.showWording("重试","网络连接失败！",Color.RED);
                break;
            case DownloadMainThread.DOWNLOAD_STATE_END:
                holder.showWording("安装","等待安装",Color.BLACK);
                holder.onFinish();
                break;
            case DownloadMainThread.DOWNLOAD_STATE_INSTALLED:
                holder.showWording("打开","",Color.BLACK);
                holder.onFinish();
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    public void sendMessage(Message msg){
        handler.sendMessage(msg);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private DownloadItemAdapter itemAdapter;
        private TextView stateText;
        private TextView speedText;
        private TextView appName;
        private TextView installText;
        private ImageView appIcon;
        private ProgressBar bar;
        private Button btn;
        private ImageView deleteBtn;
        private Dialog dialog;
        private DownloadTaskController controller;

        public MyViewHolder(View v,DownloadItemAdapter adapter) {
            super(v);
            this.itemAdapter = adapter;
            bindViews(v);
        }
        private void bindViews(View v){
            stateText = (TextView)v.findViewById(R.id.stateText);
            speedText = (TextView)v.findViewById(R.id.speedText);
            installText = (TextView)v.findViewById(R.id.installText);
            appName = (TextView)v.findViewById(R.id.appName);
            appIcon = (ImageView)v.findViewById(R.id.appIcon);
            btn = (Button)v.findViewById(R.id.download_button);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(controller == null){
                        return;
                    }
                    switch (controller.getDownloadState()){
                        case DownloadMainThread.DOWNLOAD_STATE_NEW:
                            controller.addTask();
                            break;
                        case DownloadMainThread.DOWNLOAD_STATE_RUNNABLE:
                        case DownloadMainThread.DOWNLOAD_STATE_RUNNING:
                            controller.pauseTask();
                            break;
                        case DownloadMainThread.DOWNLOAD_STATE_PAUSED:
                            controller.restart();
                            break;
                        case DownloadMainThread.DOWNLOAD_STATE_TERMINATED:
                        case DownloadMainThread.DOWNLOAD_STATE_FAILED:
                            controller.restart();
                            break;
                        case DownloadMainThread.DOWNLOAD_STATE_END:
                            ApkInfoAccessor.getInstance().apkInstallAttempt(controller.getInfo().getFileName());
                            break;
                        case DownloadMainThread.DOWNLOAD_STATE_BLOCKED:
                            break;
                        case DownloadMainThread.DOWNLOAD_STATE_INSTALLED:
                            ApkInfoAccessor.getInstance().launchApp(controller.getInfo().getPackageName());
                            break;
                        default:
                            break;
                    }
                }
            });
            bar = (ProgressBar)v.findViewById(R.id.progressBar);
            deleteBtn = (ImageView)v.findViewById(R.id.dustIcon);
            dialog = new AlertDialog.Builder(v.getContext()).setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message).setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.mThread_pool.deleteTask(controller);
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel,null).create();
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show();
                }
            });
        }

        public void setParamTag(UpdateParams params){
            controller = params.getController();
            itemView.setTag(params);
        }

        public void updateProgress(int downloadedSize,int speed,int fileSize){
            bar.setVisibility(View.VISIBLE);
            speedText.setVisibility(View.VISIBLE);
            stateText.setVisibility(View.VISIBLE);
            installText.setVisibility(View.GONE);
            String s_fileSize = String.format(Locale.CHINESE,"%.2f",fileSize/1024/1024.0);
            String s_downloadedSize = String.format(Locale.CHINESE,"%.2f",downloadedSize/1024/1024.0);
            String state = s_downloadedSize+"M/"+s_fileSize+"M";
            stateText.setText(state);
            String s_speed = String.valueOf(speed/1024)+"KB/s";
            speedText.setText(s_speed);
            bar.setMax(fileSize);
            bar.setProgress(downloadedSize);
            btn.setText("暂停");
        }
        public void onFinish(){
            appIcon.setBackground(controller.getInfo().getIcon());
            appName.setText(controller.getInfo().getName());
        }

        private void showWording(String btnText,String wording,int color){
            btn.setText(btnText);
            bar.setVisibility(View.GONE);
            speedText.setVisibility(View.INVISIBLE);
            stateText.setVisibility(View.INVISIBLE);
            installText.setVisibility(View.VISIBLE);
            installText.setText(wording);
            installText.setTextColor(color);
        }
    }
    public static class UpdateParams{
        public static final int PARAMS_LENGTH = 3;
        private DownloadTaskController controller = null;
        private Integer[] params = new Integer[PARAMS_LENGTH];
        private boolean isFinish = false;
        public UpdateParams(){
            for(int i = 0 ; i < PARAMS_LENGTH; i++){
                params[i] = 0;
            }
        }
        public void setController(DownloadTaskController controller){
            this.controller = controller;
        }
        public void updateParams(Integer[] params){
            for(int i = 0 ; i < PARAMS_LENGTH; i++){
                this.params[i] = params[i];
            }
        }
        public DownloadTaskController getController(){
            return controller;
        }
        public int getDownloadedSize(){
            return params[0];
        }
        public int getSpeed(){
            return params[1];
        }
        public int getFileSize(){
            return params[2];
        }
        public int getInfoID(){
            return controller.getInfo().getID();
        }
    }
}