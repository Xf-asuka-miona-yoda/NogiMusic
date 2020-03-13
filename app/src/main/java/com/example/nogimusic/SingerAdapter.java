package com.example.nogimusic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.ViewHolder> {
    private List<Singer> singerList;
    private Context mcontext;
    private SingerAdapter.OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView singerpic;
        TextView singername;

        View singerview;

        public ViewHolder(View view){
            super(view);
            singerview = view;
            singerpic = (ImageView) view.findViewById(R.id.singer_pic);
            singername = (TextView) view.findViewById(R.id.singer_name);
        }
    }

    public interface OnItemClickListener
    {
        //子条目单机事件
        void onItemClick(View view, int position);
    }

    public  void setmOnItemClickListener (SingerAdapter.OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public SingerAdapter(List<Singer> list, Context context){
        singerList = list;
        mcontext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singer_item, parent, false);
        final SingerAdapter.ViewHolder holder = new SingerAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Singer singer = singerList.get(position);
        Glide.with(mcontext).load(singer.getSingerpicurl()).into(holder.singerpic);
        holder.singername.setText(singer.getSingername());

        //activity调用setOnItemClickListener() 如果调用接口不为空执行下面逻辑
        if (mOnItemClickListener != null) {
            holder.singerview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //返回对应view的信息
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.singerview, pos);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return singerList.size();
    }
}
