package com.example.nogimusic;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<HistorySearch> mhistorySearchList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView historysearch_content;

        public ViewHolder(View view){
            super(view);
            historysearch_content = (TextView) view.findViewById(R.id.search_content);
        }
    }

    public SearchAdapter(List<HistorySearch> historySearchList){
        mhistorySearchList = historySearchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.historysearch_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                HistorySearch search = mhistorySearchList.get(position);
                Toast.makeText(v.getContext(),"点击了" + search.getContent(),Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistorySearch search = mhistorySearchList.get(position);
        holder.historysearch_content.setText(search.getContent());
    }

    @Override
    public int getItemCount() {
        return mhistorySearchList.size();
    }
}
