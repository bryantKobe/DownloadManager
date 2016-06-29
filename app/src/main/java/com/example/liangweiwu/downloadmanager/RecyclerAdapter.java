package com.example.liangweiwu.downloadmanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by liangwei.wu on 16/6/29.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    public ArrayList<GameInformation> datas = null;
    public RecyclerAdapter(ArrayList<GameInformation> datas) {
        this.datas = datas;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.game_information_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.mTextView.setText(datas.get(position).getName());
        viewHolder.mImageView.setBackgroundResource(datas.get(position).getIcon());
        viewHolder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("click");
                //Toast.makeText(view.getContext(),viewHolder.mTextView.getText(),Toast.LENGTH_SHORT).show();
                // TODO
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
