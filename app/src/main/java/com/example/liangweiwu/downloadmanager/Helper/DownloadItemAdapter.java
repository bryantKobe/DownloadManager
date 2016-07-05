package com.example.liangweiwu.downloadmanager.Helper;

import android.app.Dialog;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
        if(!params.isFinish()){
            holder.preProcess();
            holder.updateProgressText(params.getDownloadProgress(),params.getSpeed());
            holder.updateProgressBar(params.getFileSize(),params.getDownloadedSize());
            holder.setController(params.getController());
            holder.updateBtnState();
        }else{
            holder.onFinish();
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
            stateText.setText(state);
            speedText.setText(speed);
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
        public void onFinish(){
            btn.setText("安装");
            bar.setVisibility(View.INVISIBLE);
            speedText.setVisibility(View.INVISIBLE);
            stateText.setVisibility(View.INVISIBLE);
            installText.setVisibility(View.VISIBLE);
            isCompleted = true;
            //
            // TODO
            // get package information
            //
            GameInformation info = controller.getInfo();
            String packagePath = FileUtils.DIR_PACKAGE + info.getAttribution("package");
            System.out.println(packagePath);
            ApkInfoAccessor accessor = new ApkInfoAccessor(packagePath,itemView.getContext());
            System.out.println("accessor");
            accessor.drawPacks().debug();
        }
        public void preProcess(){
            if(!isCompleted){
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
            isFinish = true;
        }
        public boolean isFinish(){
            return isFinish;
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