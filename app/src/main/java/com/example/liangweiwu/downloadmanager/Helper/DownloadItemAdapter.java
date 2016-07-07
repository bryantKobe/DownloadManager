package com.example.liangweiwu.downloadmanager.Helper;

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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.liangweiwu.downloadmanager.Model.DownloadController;
import com.example.liangweiwu.downloadmanager.Model.DownloadTask;
import com.example.liangweiwu.downloadmanager.Model.GameInformation;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.Utils.FileUtils;
import com.example.liangweiwu.downloadmanager.Utils.GameInformationUtils;
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
        holder.preProcess();
        holder.setController(params.getController());
        holder.setID(params.getInfoID());
        if(params.isFailed()){
            holder.onFailed();
        }else if(params.isFinish()){
            holder.onFinish();
        }else{
            holder.updateProgressText(params.getDownloadProgress(),params.getSpeed());
            holder.updateProgressBar(params.getFileSize(),params.getDownloadedSize());
            if(holder.isStart()){
                //holder.onCreate();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    public void sendMessage(Message msg){
        handler.sendMessage(msg);
    }
    public void deleteTask(int id){
        for(UpdateParams params : mDatas){
            if(params.getInfoID() == id){
                mDatas.remove(params);
                break;
            }
        }
        notifyDataSetChanged();
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
        private DownloadController controller;
        private int InfoId = GameInformation.EMPTY_ID;
        private boolean isCompleted = false;
        private boolean isFailed = false;
        private boolean isStart = false;

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
                    if(controller.isFinish()){
                        ApkInfoAccessor.getInstance().apkInstall((String) controller.getInfo().getAttribution("package"));
                        return;
                    }
                    switch (controller.getDownloadState()){
                        case DownloadTask.DOWNLOAD_STATE_NEW:
                            controller.start();
                            isStart = true;
                            break;
                        case DownloadTask.DOWNLOAD_STATE_RUNNABLE:
                        case DownloadTask.DOWNLOAD_STATE_RUNNING:
                            controller.stop();
                            isStart = false;
                            break;
                        case DownloadTask.DOWNLOAD_STATE_PAUSED:
                            controller.restart();
                            isStart = true;
                            break;
                        case DownloadTask.DOWNLOAD_STATE_TERMINATED:
                        case DownloadTask.DOWNLOAD_STATE_FAILED:
                            controller.restart();
                            isStart = true;
                            break;
                        case DownloadTask.DOWNLOAD_STATE_END:
                            ApkInfoAccessor.getInstance().apkInstall((String) controller.getInfo().getAttribution("package"));
                            break;
                        case DownloadTask.DOWNLOAD_STATE_BLOCKED:
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
                            controller.stop();
                            String fileName = (String) GameInformationUtils.getInstance().getGameInfoByID(InfoId).getAttribution("package");
                            if(FileUtils.deleteApk(fileName)){
                                GameInformationUtils.getInstance().delete(InfoId);
                            }else{
                                Toast.makeText(itemView.getContext(),"删除失败",Toast.LENGTH_SHORT).show();
                            }
                            itemAdapter.deleteTask(InfoId);
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel,null).create();
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show();
                }
            });
            showWording("等待下载",Color.BLACK);
            btn.setText("等待中");
        }
        public void setID(int id){
            if(InfoId != id){
                InfoId = id;
            }
        }
        public void updateProgressText(String state,String speed){
            btn.setText("暂停");
            speedText.setText(speed);
            stateText.setText(state);
        }
        public void updateProgressBar(int fileSize,int downloadedSize){
            bar.setMax(fileSize);
            bar.setProgress(downloadedSize);
        }

        public void setController(DownloadController controller){
            this.controller = controller;
        }
        public void onCreate(){
            if(controller.getDownloadedSize() == 0) {
                btn.setText("等待中");
                showWording("等待下载", Color.BLACK);
            }else{
                btn.setText("继续");
                showWording("已暂停",Color.BLACK);
            }
        }
        public boolean isStart(){
            boolean temp = isStart;
            isStart = true;
            return temp;
        }
        public void onFinish(){
            isCompleted = true;
            btn.setText("安装");
            showWording("等待安装",Color.BLACK);
            //
            // TODO
            // get package information
            //
            GameInformationUtils.getInstance().onDownloadedFinish(controller.getInfo());
            appIcon.setBackground(controller.getInfo().getIcon());
            appName.setText(controller.getInfo().getName());
        }
        public void onFailed(){
            isFailed = true;
            btn.setText("重试");
            showWording("网络连接错误!",Color.RED);
        }
        private void showWording(String str,int color){
            bar.setVisibility(View.GONE);
            speedText.setVisibility(View.INVISIBLE);
            stateText.setVisibility(View.INVISIBLE);
            installText.setVisibility(View.VISIBLE);
            installText.setText(str);
            installText.setTextColor(color);
        }
        public void preProcess(){
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
            if(controller.getDownloadState() == DownloadTask.DOWNLOAD_STATE_FAILED
                    || controller.getDownloadState() == DownloadTask.DOWNLOAD_STATE_TERMINATED){
                return;
            }
            isFinish = true;
        }
        public boolean isFinish(){
            return isFinish || controller.isFinish();
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
        public int getInfoID(){
            return controller.getInfo().getID();
        }
    }
}