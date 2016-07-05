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

import com.example.liangweiwu.downloadmanager.Model.GameInformation;
import com.example.liangweiwu.downloadmanager.R;
import java.util.List;


public class DownloadItemAdapter extends RecyclerView.Adapter<DownloadItemAdapter.MyViewHolder> {

    public List<GameInformation> mDatas = null;
    public Handler handler = null;

    public DownloadItemAdapter(List<GameInformation> mDatas) {
        this.mDatas = mDatas;
    }

    public DownloadItemAdapter(List<GameInformation> mDatas, Handler handler) {
        this.mDatas = mDatas;
        this.handler = handler;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_manager_item_layout, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TextView stateText = (TextView)holder.itemView.findViewById(R.id.stateText);
        TextView speedText = (TextView)holder.itemView.findViewById(R.id.speedText);
        TextView appName = (TextView)holder.itemView.findViewById(R.id.appName);
        ImageView appIcon = (ImageView)holder.itemView.findViewById(R.id.appIcon);
        Button btn = (Button)holder.itemView.findViewById(R.id.download_button);
        ProgressBar bar = (ProgressBar)holder.itemView.findViewById(R.id.progressBar);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View v) {
            super(v);

            final Dialog dialog = new AlertDialog.Builder(v.getContext()).setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message).setPositiveButton(R.string.dialog_ok,null)
                    .setNegativeButton(R.string.dialog_cancel,null).create();
            ImageView dustbin = (ImageView)v.findViewById(R.id.dustIcon);
            dustbin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show();
                }
            });
        }
    }
}