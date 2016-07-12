package com.example.liangweiwu.downloadmanager.view.controller;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liangweiwu.downloadmanager.util.ApkInfoAccessor;
import com.example.liangweiwu.downloadmanager.model.DownloadTaskController;
import com.example.liangweiwu.downloadmanager.thread.DownloadMainThread;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.thread.DownloadTaskPoolThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by nol & xinxin.li
 */
public class DownloadItemAdapter extends RecyclerView.Adapter<DownloadItemAdapter.MyViewHolder> {

    private ArrayList<ViewController> mDatas;
    private SimpleItemTouchHelperCallback mCallback;

    public DownloadItemAdapter(ArrayList<ViewController> mDatas) {
        this.mDatas = mDatas;
        mCallback = new SimpleItemTouchHelperCallback();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_manager_item_layout, parent, false), this);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ViewController viewController = mDatas.get(position);
        holder.setParamTag(viewController);
        int state = viewController.getController().getDownloadState();
        switch (state) {
            case DownloadMainThread.DOWNLOAD_STATE_NEW:
                holder.showWording("等待中", "等待下载", Color.BLACK);
                break;
            case DownloadMainThread.DOWNLOAD_STATE_RUNNABLE:
            case DownloadMainThread.DOWNLOAD_STATE_RUNNING:
                holder.updateProgress(viewController.getDownloadedSize(), viewController.getSpeed(), viewController.getFileSize());
                break;
            case DownloadMainThread.DOWNLOAD_STATE_PAUSED:
            case DownloadMainThread.DOWNLOAD_STATE_TERMINATED:
                holder.showWording("继续", "已暂停", Color.BLACK);
                break;
            case DownloadMainThread.DOWNLOAD_STATE_FAILED:
                holder.showWording("重试", "网络连接失败！", Color.RED);
                break;
            case DownloadMainThread.DOWNLOAD_STATE_END:
                holder.showWording("安装", "等待安装", Color.BLACK);
                holder.onFinish();
                break;
            case DownloadMainThread.DOWNLOAD_STATE_INSTALLED:
                holder.showWording("打开", "", Color.BLACK);
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

        public MyViewHolder(View v, DownloadItemAdapter adapter) {
            super(v);
            this.itemAdapter = adapter;
            bindViews(v);
        }

        private void bindViews(View v) {
            stateText = (TextView) v.findViewById(R.id.stateText);
            speedText = (TextView) v.findViewById(R.id.speedText);
            installText = (TextView) v.findViewById(R.id.installText);
            appName = (TextView) v.findViewById(R.id.appName);
            appIcon = (ImageView) v.findViewById(R.id.appIcon);
            btn = (Button) v.findViewById(R.id.download_button);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (controller == null) {
                        return;
                    }
                    switch (controller.getDownloadState()) {
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
            bar = (ProgressBar) v.findViewById(R.id.progressBar);
            deleteBtn = (ImageView) v.findViewById(R.id.dustIcon);
            dialog = new AlertDialog.Builder(v.getContext()).setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message).setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DownloadTaskPoolThread.getInstance().deleteTask(controller);
                            notifyItemRemoved(getLayoutPosition());
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, null).create();
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show();
                }
            });
        }

        public void setParamTag(ViewController params) {
            controller = params.getController();
            itemView.setTag(params);
        }

        public void updateProgress(int downloadedSize, int speed, int fileSize) {
            bar.setVisibility(View.VISIBLE);
            speedText.setVisibility(View.VISIBLE);
            stateText.setVisibility(View.VISIBLE);
            installText.setVisibility(View.GONE);
            String s_fileSize = String.format(Locale.CHINESE, "%.2f", fileSize / 1024 / 1024.0);
            String s_downloadedSize = String.format(Locale.CHINESE, "%.2f", downloadedSize / 1024 / 1024.0);
            String state = s_downloadedSize + "M/" + s_fileSize + "M";
            stateText.setText(state);
            String s_speed = String.valueOf(speed / 1024) + "KB/s";
            speedText.setText(s_speed);
            bar.setMax(fileSize);
            bar.setProgress(downloadedSize);
            btn.setText("暂停");
        }

        public void onFinish() {
            appIcon.setBackground(controller.getInfo().getIcon());
            appName.setText(controller.getInfo().getName());
        }

        private void showWording(String btnText, String wording, int color) {
            btn.setText(btnText);
            bar.setVisibility(View.GONE);
            speedText.setVisibility(View.INVISIBLE);
            stateText.setVisibility(View.INVISIBLE);
            installText.setVisibility(View.VISIBLE);
            installText.setText(wording);
            installText.setTextColor(color);
        }
    }

    public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = 0;
            int swipeFlags = 0;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
                        | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int src = viewHolder.getAdapterPosition();
            int tar = target.getAdapterPosition();
            if (src == tar) {
                return false;
            }
            Collections.swap(mDatas, src, tar);
            notifyItemMoved(src, tar);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }
    }

    public void notifyVisibleDataChanged(int src, int tar) {
        for (int i = src; i <= tar; i++) {
            notifyItemChanged(i);
        }
    }

    public SimpleItemTouchHelperCallback getCallback() {
        return mCallback;
    }
}