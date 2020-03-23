package com.example.nogimusic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.ViewHolder> {
    private List<Dynamic> dynamicList;
    private Context mcontext;
    private DynamicAdapter.OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View dynamicview;
        ImageView userimg;
        TextView user_name;
        TextView time;
        TextView content;
        TextView zhuanfa;
        TextView pinglun;
        TextView dianzan;

        public ViewHolder(View view){
            super(view);
            dynamicview = view;
            userimg = (ImageView) view.findViewById(R.id.dy_user_img);
            user_name = (TextView) view.findViewById(R.id.dynamic_username);
            time = (TextView) view.findViewById(R.id.dynamic_time);
            content = (TextView) view.findViewById(R.id.dynamic_content);
            zhuanfa = (TextView) view.findViewById(R.id.zhuanfa);
            pinglun = (TextView) view.findViewById(R.id.pinglun);
            dianzan = (TextView) view.findViewById(R.id.dianzan);
        }
    }

    public interface OnItemClickListener
    {
        //子条目单机事件
        void onItemClick(View view, int position);
    }

    public  void setmOnItemClickListener (DynamicAdapter.OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public DynamicAdapter(List<Dynamic> list, Context context){
        dynamicList = list;
        mcontext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_item, parent, false);
        final DynamicAdapter.ViewHolder holder = new DynamicAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Dynamic dynamic = dynamicList.get(position);
        holder.user_name.setText(dynamic.getUsername());
        holder.time.setText(dynamic.gettime());
        holder.content.setText(dynamic.getContent());
        holder.zhuanfa.setText("转发:" + dynamic.getZhuanfa());
        holder.pinglun.setText("评论:" + dynamic.getPinglun());
        holder.dianzan.setText("点赞:" + dynamic.getDianzan());

        //activity调用setOnItemClickListener() 如果调用接口不为空执行下面逻辑
        if (mOnItemClickListener != null) {
            holder.dynamicview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //返回对应view的信息
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.dynamicview, pos);
                }
            });

            holder.zhuanfa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.zhuanfa, pos);
                }
            });

            holder.dianzan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.dianzan, pos);
                }
            });

            holder.userimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.userimg, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dynamicList.size();
    }
}
