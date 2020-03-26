package com.example.nogimusic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HotSearchAdapter extends RecyclerView.Adapter<HotSearchAdapter.ViewHolder> {
    private List<HotSearch> searchList;
    private OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView number;
        View searchview;

        public ViewHolder(View view){
            super(view);
            searchview = view;
            content = (TextView) view.findViewById(R.id.hot_search_content);
            number = (TextView) view.findViewById(R.id.hot_search_num);
        }
    }

    public interface OnItemClickListener
    {
        //子条目单机事件
        void onItemClick(View view, int position);
    }

    public  void setmOnItemClickListener (OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public HotSearchAdapter(List<HotSearch> hotSearchList){
        this.searchList = hotSearchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotsearch_item, parent, false);
        final ViewHolder holder = new HotSearchAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        HotSearch hotSearch = searchList.get(position);
        holder.content.setText(hotSearch.getContent());
        holder.number.setText(hotSearch.getNum());

        //activity调用setOnItemClickListener() 如果调用接口不为空执行下面逻辑
        if (mOnItemClickListener != null) {
            holder.searchview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //返回对应view的信息
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.searchview, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }
}
