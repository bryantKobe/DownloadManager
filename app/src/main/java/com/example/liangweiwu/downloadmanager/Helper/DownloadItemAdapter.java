package com.example.liangweiwu.downloadmanager.Helper;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.liangweiwu.downloadmanager.Model.DownloadController;
import com.example.liangweiwu.downloadmanager.Model.DownloadTask;
import com.example.liangweiwu.downloadmanager.Model.GameInformation;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.Utils.FileUtils;

import java.util.List;
import java.util.Locale;


public class DownloadItemAdapter extends RecyclerView.Adapter<DownloadItemAdapter.MyViewHolder> {

    public List<UpdateParams> mDatas = null;
    public Handler handler = null;

    public DownloadItemAdapter(List<UpdateParams> mDatas) {
        this.mDatas = mDatas;
    }

    public DownloadItemAdapter(List<UpdateParams> mDatas, Handler handler) {
        this.mDatas = mDatas;
        this.handler = handler;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_manager_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final UpdateParams params = mDatas.get(position);
        holder.preProcess();
        holder.setController(params.getController());
        holder.updateBtnState();
        if(params.isFailed()){
            holder.onFailed();
        }else if(params.isFinish()){
            holder.onFinish();
        }else{
            holder.updateProgressText(params.getDownloadProgress(),params.getSpeed());
            holder.updateProgressBar(params.getFileSize(),params.getDownloadedSize());
            if(params.isNew()){
                holder.onCreate(params.getController().getDownloadedSize(),params.getController().getFileSize());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView stateText;
        private TextView speedText;
        private TextView appName;
        private TextView installText;
        private ImageView appIcon;
        private ProgressBar bar;
        private Button btn;
        private ImageView deleteBtn;
        private Dialog dialog;
        private DownloadController controller;
        private boolean isCompleted = false;
        private boolean isFailed = false;

        public MyViewHolder(View v) {
            super(v);
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
                    if(controller.isFinish()){
                        ApkInfoAccessor.getInstance().apkInstall((String) controller.getInfo().getAttribution("package"));
                        return;
                    }
                    switch (controller.getDownloadState()){
                        case DownloadTask.DOWNLOAD_STATE_NEW:
                            controller.start();
                            break;
                        case DownloadTask.DOWNLOAD_STATE_RUNNABLE:
                        case DownloadTask.DOWNLOAD_STATE_RUNNING:
                            controller.pause();
                            break;
                        case DownloadTask.DOWNLOAD_STATE_PAUSED:
                            controller.resume();
                            break;
                        case DownloadTask.DOWNLOAD_STATE_TERMINATED:
                        case DownloadTask.DOWNLOAD_STATE_FAILED:
                            controller.restart();
                            break;
                        case DownloadTask.DOWNLOAD_STATE_END:
                            ApkInfoAccessor.getInstance().apkInstall((String) controller.getInfo().getAttribution("package"));
                            break;
                        default:
                            break;
                    }
                }
            });
            bar = (ProgressBar)v.findViewById(R.id.progressBar);
            deleteBtn = (ImageView)v.findViewById(R.id.dustIcon);
            dialog = new AlertDialog.Builder(v.getContext()).setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message).setPositiveButton(R.string.dialog_ok,null)
                    .setNegativeButton(R.string.dialog_cancel,null).create();
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show();
                }
            });
        }
        public void updateProgressText(String state,String speed){
            if(controller.getDownloadState() == DownloadTask.DOWNLOAD_STATE_PAUSED){
                String str = "0KB/s";
                speedText.setText(str);
            }else{
                speedText.setText(speed);
            }
            stateText.setText(state);
        }
        public void updateProgressBar(int fileSize,int downloadedSize){
            bar.setMax(fileSize);
            bar.setProgress(downloadedSize);
        }
        public void updateBtnState(){
            if(controller == null){
                return;
            }
            switch (controller.getDownloadState()){
                case DownloadTask.DOWNLOAD_STATE_NEW:
                    btn.setText("等待中");
                    break;
                case DownloadTask.DOWNLOAD_STATE_RUNNABLE:
                case DownloadTask.DOWNLOAD_STATE_RUNNING:
                    btn.setText("暂停");
                    break;
                case DownloadTask.DOWNLOAD_STATE_PAUSED:
                case DownloadTask.DOWNLOAD_STATE_TERMINATED:
                case DownloadTask.DOWNLOAD_STATE_FAILED:
                    btn.setText("继续");
                    break;
                case DownloadTask.DOWNLOAD_STATE_END:
                    btn.setText("安装");
                    break;
                default:
                    break;
            }
        }
        public void setController(DownloadController controller){
            this.controller = controller;
        }
        public void onCreate(int downloadedSize,int fileSize){
            if(downloadedSize != 0){
                btn.setText("继续");
            }
            bar.setMax(fileSize);
            bar.setProgress(downloadedSize);

            String str1 = String.format(Locale.CHINESE,"%.2f",downloadedSize/1024/1024.0) + "M/" +
                    String.format(Locale.CHINESE,"%.2f",fileSize/1024/1024.0) + "M";
            stateText.setText(str1);
        }
        public void onFinish(){
            isCompleted = true;
            btn.setText("安装");
            bar.setVisibility(View.INVISIBLE);
            speedText.setVisibility(View.INVISIBLE);
            stateText.setVisibility(View.INVISIBLE);
            installText.setVisibility(View.VISIBLE);
            installText.setText("等待安装");
            installText.setTextColor(Color.GRAY);
            //
            // TODO
            // get package information
            //
            GameInformation info = ApkInfoAccessor.getInstance().drawPackages("com.DBGame.DiabloLOL.apk",controller.getInfo());
            appIcon.setBackground(info.getIcon());
            appName.setText(info.getName());
        }
        public void onFailed(){
            isFailed = true;
            bar.setVisibility(View.INVISIBLE);
            speedText.setVisibility(View.INVISIBLE);
            stateText.setVisibility(View.INVISIBLE);
            installText.setVisibility(View.VISIBLE);
            installText.setText("网络连接错误!");
            installText.setTextColor(Color.RED);
        }
        public void preProcess(){
            if(!(isCompleted || isFailed)){
                return;
            }
            bar.setVisibility(View.VISIBLE);
            speedText.setVisibility(View.VISIBLE);
            stateText.setVisibility(View.VISIBLE);
            installText.setVisibility(View.GONE);
        }


    }
    public static class UpdateParams{
        public static final int PARAMS_LENGTH = 3;
        private DownloadController controller = null;
        private Integer[] params = new Integer[PARAMS_LENGTH];
        private boolean isFinish = false;
        private boolean isNew = true;
        public UpdateParams(){
            for(int i = 0 ; i < PARAMS_LENGTH; i++){
                params[i] = 0;
            }
        }
        public void setController(DownloadController controller){
            this.controller = controller;
        }
        public void updateParams(Integer[] params){
            for(int i = 0 ; i < PARAMS_LENGTH; i++){
                this.params[i] = params[i];
            }
        }
        public void setFinished(){
            if(controller.getDownloadState() == DownloadTask.DOWNLOAD_STATE_FAILED
                    || controller.getDownloadState() == DownloadTask.DOWNLOAD_STATE_TERMINATED){
                return;
            }
            isFinish = true;
        }
        public boolean isFinish(){
            return isFinish || controller.isFinish();
        }
        public boolean isNew(){
            boolean temp = isNew;
            isNew = false;
            return temp;
        }
        public boolean isFailed(){
            return controller.getDownloadState() == DownloadTask.DOWNLOAD_STATE_FAILED;

        }
        public DownloadController getController(){
            return controller;
        }
        public String getDownloadProgress(){
            String fileSize = String.format(Locale.CHINESE,"%.2f",params[2]/1024/1024.0);
            String downloadedSize = String.format(Locale.CHINESE,"%.2f",params[0]/1024/1024.0);
            return downloadedSize + "M/" + fileSize + "M";
        }
        public String getSpeed(){
            return String.valueOf(params[1]/1024) + "KB/s";
        }
        public int getFileSize(){
            return params[2];
        }
        public int getDownloadedSize(){
            return params[0];
        }
    }
}