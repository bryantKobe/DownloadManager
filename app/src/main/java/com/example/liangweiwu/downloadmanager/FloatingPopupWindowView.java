package com.example.liangweiwu.downloadmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by liangwei.wu on 16/6/29.
 */
public class FloatingPopupWindowView extends LinearLayout {
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
    private RecyclerAdapter mAdapter;

    public FloatingPopupWindowView(final Context context) {
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
        ArrayList<GameInformation> datas = getDatas();
        mAdapter = new RecyclerAdapter(datas);
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<GameInformation> getDatas(){
        ArrayList<GameInformation> datas = new ArrayList<>();
        datas.add(new GameInformation("TemplateRun",R.drawable.lajixiang));
        datas.add(new GameInformation("TemplateRun2",R.drawable.lajixiang));
        datas.add(new GameInformation("TemplateRun3",R.drawable.lajixiang));
        datas.add(new GameInformation("TemplateRun4",R.drawable.lajixiang));
        datas.add(new GameInformation("TemplateRun5",R.drawable.lajixiang));
        return datas;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        System.out.println("touch1");
        return false;
    }
}
