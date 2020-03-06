package com.example.nogimusic;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HomeicAdapter extends RecyclerView.Adapter<HomeicAdapter.ViewHolder> {
    private List<home_icon> mhomeic;
    private OnItemClickListener mOnItemClickListener;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View ic_view;
        ImageView ic_img;
        TextView ic_name;

        public ViewHolder(View view){
            super(view);
            ic_view = view;
            ic_img = (ImageView) view.findViewById(R.id.home_ic_img);
            ic_name = (TextView) view.findViewById(R.id.home_ic_name);
        }
    }

    public interface OnItemClickListener
    {
        //子条目单机事件
        void onItemClick(View view, int position);
    }

    public  void setmOnItemClickListener (OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener= mOnItemClickListener;
    }

    public HomeicAdapter(List<home_icon> home_icons){
        mhomeic = home_icons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_icon, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        home_icon home_ic = mhomeic.get(position);
        holder.ic_img.setImageResource(home_ic.getImgid());
        holder.ic_name.setText(home_ic.getIc_name());

        //activity调用setOnItemClickListener() 如果调用接口不为空执行下面逻辑
        if (mOnItemClickListener != null) {
            holder.ic_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //返回对应view的信息
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.ic_view, pos);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mhomeic.size();
    }
}
