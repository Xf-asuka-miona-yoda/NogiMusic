package com.example.nogimusic;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MymessageAdapter extends RecyclerView.Adapter<MymessageAdapter.ViewHolder> {
    private List<Mymessage> mymessageList;
    private OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View msgview;
        TextView username;
        TextView time;
        TextView type;
        ImageView userimg;
        TextView content;

        public ViewHolder(View view){
            super(view);
            msgview = view;
            userimg = (ImageView) view.findViewById(R.id.msg_user_img);
            username = (TextView) view.findViewById(R.id.msg_user_name);
            time = (TextView) view.findViewById(R.id.msg_user_time);
            type = (TextView) view.findViewById(R.id.msg_type);
            content = (TextView) view.findViewById(R.id.msg_content);
        }
    }

    public interface OnItemClickListener
    {
        //子条目单击事件
        void onItemClick(View view, int position);
    }

    public  void setmOnItemClickListener (OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public MymessageAdapter(List<Mymessage> mymessages){
        this.mymessageList = mymessages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mymessage_item, parent, false);
        final MymessageAdapter.ViewHolder viewHolder = new MymessageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Mymessage mymessage = mymessageList.get(position);
        holder.username.setText(mymessage.getUsername());
        holder.content.setText(mymessage.getContent());
        if (mymessage.getType().equals("dycomment")){
            holder.type.setText("评论了您的动态");
        }
        holder.time.setText(mymessage.getTime());


        if (mOnItemClickListener != null) {
            holder.msgview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //返回对应view的信息
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.msgview, pos);
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
        return mymessageList.size();
    }
}
