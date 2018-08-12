package com.summertaker.stocknews;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.summertaker.stocknews.common.Config;
import com.summertaker.stocknews.data.News;

import java.util.ArrayList;

public class ItemNewsListAdapter extends RecyclerView.Adapter<ItemNewsListAdapter.ItemViewHolder> {

    private ArrayList<News> mNewsList;
    private String mName;

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvPublished;

        public ItemViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tvTitle);
            tvPublished = view.findViewById(R.id.tvPublished);
        }
    }

    public ItemNewsListAdapter(ArrayList<News> newsList, String name) {
        this.mNewsList = newsList;
        this.mName = name;
    }

    @Override
    public ItemNewsListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_list_row, parent, false);
        return new ItemNewsListAdapter.ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemNewsListAdapter.ItemViewHolder holder, int position) {
        News news = mNewsList.get(position);

        // 제목
        String title = news.getTitle();
        title = news.getId() + ". " + title;
        title = title.replace(mName, String.format(Config.NEWS_ITEM_NAME_HIGHLIGHT_FORMAT, mName));
        holder.tvTitle.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);

        // 발행일
        holder.tvPublished.setText(news.getPublished());
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
