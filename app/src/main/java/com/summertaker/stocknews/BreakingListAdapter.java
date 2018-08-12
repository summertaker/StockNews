package com.summertaker.stocknews;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.summertaker.stocknews.common.Config;
import com.summertaker.stocknews.data.News;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BreakingListAdapter extends RecyclerView.Adapter<BreakingListAdapter.ItemViewHolder> {

    private ArrayList<News> mNewsList;

    public BreakingListAdapter(ArrayList<News> newsList) {
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
    public BreakingListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.breaking_list_row, parent, false);
        return new BreakingListAdapter.ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BreakingListAdapter.ItemViewHolder holder, int position) {
        News news = mNewsList.get(position);

        // 제목
        String title = news.getTitle();
        title = news.getId() + ". " + title;
        holder.tvTitle.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);

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
