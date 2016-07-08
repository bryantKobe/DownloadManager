package com.example.liangweiwu.downloadmanager.helper;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.liangweiwu.downloadmanager.model.GameInformation;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.views.FloatingPopupWindowView;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    public ArrayList<GameInformation> datas = null;
    public Handler mHandler;
    public RecyclerAdapter(ArrayList<GameInformation> datas,Handler handler) {
        this.datas = datas;
        this.mHandler = handler;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.game_information_item,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final GameInformation info = datas.get(position);
        viewHolder.mTextView.setText(info.getName());
        Drawable icon = info.getIcon();
        if(icon == null){
            viewHolder.mImageView.setBackgroundResource(R.drawable.default_icon);
        }else{
            viewHolder.mImageView.setBackground(info.getIcon().getConstantState().newDrawable());
        }
        viewHolder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("motion","game detail");
                // TODO
                mHandler.sendMessage(mHandler.obtainMessage(FloatingPopupWindowView.SHOW_GAME_DETAIL,info.getID()));
            }
        });
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mItemLayout;
        public ImageView mImageView;
        public TextView mTextView;
        public ViewHolder(View view){
            super(view);
            mItemLayout = (LinearLayout)view.findViewById(R.id.floating_game_item_layout);
            mImageView = (ImageView)view.findViewById(R.id.floating_game_information_icon);
            mTextView = (TextView) view.findViewById(R.id.floating_game_information_name);
        }
    }
}
