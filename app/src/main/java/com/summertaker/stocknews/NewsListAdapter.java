package com.summertaker.stocknews;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.summertaker.stocknews.data.News;

import java.util.ArrayList;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ItemViewHolder> {

    private ArrayList<News> mNewsList;

    public NewsListAdapter(ArrayList<News> newsList) {
        this.mNewsList = newsList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvPublished;

        private ItemViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tvTitle);
            tvPublished = view.findViewById(R.id.tvPublished);
        }
    }

    @NonNull
    @Override
    public NewsListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_row, parent, false);
        return new NewsListAdapter.ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsListAdapter.ItemViewHolder holder, int position) {
        News news = mNewsList.get(position);

        // 제목
        String title = news.getTitle();
        //title = news.getId() + ". " + title;
        holder.tvTitle.setText(title);

        // 발행일
        String published = news.getPublished();
        holder.tvPublished.setText(published);
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    @Override
    public long getItemId(int position) {
        return mNewsList.get(position).getId();
    }
}
