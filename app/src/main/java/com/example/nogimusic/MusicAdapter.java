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

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private List<Music> musicList;
    private Context mcontext;
    private OnItemClickListener mOnItemClickListener;



    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView musicpic;
        TextView musicname;
        TextView musicsinger;
        View musicview;

        public ViewHolder(View view){
            super(view);
            musicview = view;
            musicpic = (ImageView) view.findViewById(R.id.music_pic);
            musicname = (TextView) view.findViewById(R.id.music_name);
            musicsinger = (TextView) view.findViewById(R.id.singer);
        }
    }

    public interface OnItemClickListener
    {
        //子条目单机事件
        void onItemClick(View view, int position);
    }

    public  void setmOnItemClickListener (MusicAdapter.OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public MusicAdapter(List<Music> list, Context context){
        musicList = list;
        mcontext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        final MusicAdapter.ViewHolder holder = new MusicAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Music music = musicList.get(position);
        Glide.with(mcontext).load(music.getMusic_pic_url()).into(holder.musicpic);
        holder.musicname.setText(music.getMusic_name());
        holder.musicsinger.setText(music.getMusic_singer());

        //activity调用setOnItemClickListener() 如果调用接口不为空执行下面逻辑
        if (mOnItemClickListener != null) {
            holder.musicview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //返回对应view的信息
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.musicview, pos);
                }
            });

            holder.musicpic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.musicpic, pos);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }
}
