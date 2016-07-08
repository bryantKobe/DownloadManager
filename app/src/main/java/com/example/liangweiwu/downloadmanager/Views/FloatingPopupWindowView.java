package com.example.liangweiwu.downloadmanager.views;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liangweiwu.downloadmanager.model.GameInformation;
import com.example.liangweiwu.downloadmanager.utils.FloatingWindowManager;
import com.example.liangweiwu.downloadmanager.utils.GameInformationUtils;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.helper.RecyclerAdapter;

import java.util.ArrayList;


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
    public static final int SHOW_GAME_DETAIL = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case SHOW_GAME_DETAIL:
                    LinearLayout rootLayout = (LinearLayout) findViewById(R.id.floating_popWin_layout);
                    rootLayout.removeAllViews();
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.game_information_layout,null);
                    rootLayout.addView(view);
                    LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.detail_layout);
                    int id = (int)msg.obj;
                    GameInformation info = GameInformationUtils.getInstance().getGameInfoByID(id);
                    for(Pair<String,String> pair : info.getAttributions()){
                        addField(itemLayout,pair);
                    }
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
        mAdapter = new RecyclerAdapter(datas,mHandler);
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<GameInformation> getDatas(){
        return GameInformationUtils.getInstance().getDownloadedGamelist();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_OUTSIDE:
                Log.i("motion","touch outside");
                FloatingWindowManager.createFloatingIcon(getContext());
                FloatingWindowManager.removePopupWindow(getContext());
                break;
            default:
                break;
        }
        return true;
    }
}
