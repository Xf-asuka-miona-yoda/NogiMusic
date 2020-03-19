package com.example.nogimusic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter <ClassAdapter.ViewHolder>{
    private Context mcontext;
    private List<class_item> class_items;

    private ClassAdapter.OnItemClickListener mOnItemClickListener;



    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView classimg;
        TextView classname;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            classimg = (ImageView) view.findViewById(R.id.class_img);
            classname = (TextView) view.findViewById(R.id.class_name);
        }

    }

    public interface OnItemClickListener
    {
        //子条目单机事件
        void onItemClick(View view, int position);
    }

    public  void setmOnItemClickListener (ClassAdapter.OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public ClassAdapter(List<class_item> class_itemList){
        class_items = class_itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mcontext == null){
            mcontext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item_layout, parent, false);
        final ClassAdapter.ViewHolder holder = new ClassAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        class_item classItem = class_items.get(position);
        holder.classname.setText(classItem.getName());
        Glide.with(mcontext).load(classItem.getImgid()).into(holder.classimg);

        if (mOnItemClickListener != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //返回对应view的信息
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.cardView, pos);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return class_items.size();
    }
}
