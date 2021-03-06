package com.example.liangweiwu.downloadmanager.view.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liangweiwu.downloadmanager.util.ApkInfoAccessor;
import com.example.liangweiwu.downloadmanager.model.ApkInformation;
import com.example.liangweiwu.downloadmanager.util.FloatingWindowManager;
import com.example.liangweiwu.downloadmanager.util.ApkInfoUtils;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.view.controller.FloatingPopWinAdapter;

import java.util.ArrayList;

/**
 *  Created by Nol
 */
public class FloatingPopWin extends LinearLayout {
    /**
     * 记录大悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;
    private static int GRID_COLUMNS = 2;

    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private FloatingPopWinAdapter mAdapter;
    public static final int SHOW_GAME_DETAIL = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case SHOW_GAME_DETAIL:
                    LinearLayout rootLayout = (LinearLayout) findViewById(R.id.floating_popWin_content);
                    rootLayout.removeAllViews();
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.game_information_layout,null);
                    rootLayout.addView(view);
                    LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.detail_layout);
                    int id = (int)msg.obj;
                    final ApkInformation info = ApkInfoUtils.getInstance().getGameInfoByID(id);
                    for(Pair<String,String> pair : info.getAttributions()){
                        addField(itemLayout,pair);
                    }
                    final Button installBtn = (Button) view.findViewById(R.id.install_btn);
                    if(info.isInstalled()){
                        installBtn.setText("打开");
                    }else{
                        installBtn.setText("安装");
                    }
                    installBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(info.isInstalled()){
                                ApkInfoAccessor.getInstance().launchApp(info.getPackageName());
                            }else{
                                FloatingWindowManager.removePopupWindow(getContext());
                                FloatingWindowManager.createFloatingIcon(getContext());
                                ApkInfoAccessor.getInstance().apkInstallAttempt(info.getFileName());
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };
    private void addField(LinearLayout layout, Pair<String,String> pair){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.game_information_detail_item,null);
        ((TextView)view.findViewById(R.id.game_information_detail_title)).setText(pair.first);
        ((TextView)view.findViewById(R.id.game_information_detail_content)).setText(pair.second);
        layout.addView(view);
    }
    public FloatingPopWin(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.floating_popup_window, this);
        View view = findViewById(R.id.floating_popWin_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        mRecyclerView = (RecyclerView)findViewById(R.id.floating_recycler);
        mLayoutManager = new GridLayoutManager(context,GRID_COLUMNS);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        ArrayList<ApkInformation> datas = getDatas();
        mAdapter = new FloatingPopWinAdapter(datas,mHandler);
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<ApkInformation> getDatas(){
        return ApkInfoUtils.getInstance().getDownloadedGamelist();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i("motion","touch");
                FloatingWindowManager.createFloatingIcon(getContext());
                FloatingWindowManager.removePopupWindow(getContext());
                break;
            default:
                break;
        }
        return true;
    }
}
