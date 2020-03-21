package com.example.nogimusic;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private List<Comment> commentList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        TextView time;
        TextView content;

        public ViewHolder(View view){
            super(view);
            username = (TextView) view.findViewById(R.id.comment_username);
            time = (TextView) view.findViewById(R.id.comment_time);
            content = (TextView) view.findViewById(R.id.comment_content);
        }
    }

    public CommentsAdapter(List<Comment> comments){
        commentList = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.username.setText(comment.getUsername());
        holder.time.setText(comment.gettime());
        holder.content.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
