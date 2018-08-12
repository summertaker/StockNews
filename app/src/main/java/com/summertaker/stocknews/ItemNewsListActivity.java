package com.summertaker.stocknews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.stocknews.common.BaseActivity;
import com.summertaker.stocknews.common.BaseApplication;
import com.summertaker.stocknews.common.Config;
import com.summertaker.stocknews.data.News;
import com.summertaker.stocknews.parser.DaumNewsParser;
import com.summertaker.stocknews.util.RecyclerTouchListener;

import java.util.ArrayList;

public class ItemNewsListActivity extends BaseActivity {

    private String mCode;
    private String mName;

    private ArrayList<News> mNewsList = new ArrayList<>();
    private ItemNewsListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_news_list_activity);

        mContext = ItemNewsListActivity.this;
        initBaseActivity(mContext);

        Intent intent = getIntent();
        mCode = intent.getStringExtra("code");
        mName = intent.getStringExtra("name");
        setActionBarTitle(mName);

        mAdapter = new ItemNewsListAdapter(mNewsList, mName);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL)); // Divider
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(mContext, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                News news = mNewsList.get(position);
                Intent intent = new Intent(mContext, ItemNewsDetailActivity.class);
                intent.putExtra("code", mCode);
                intent.putExtra("name", mName);
                intent.putExtra("title", news.getTitle());
                intent.putExtra("url", news.getUrl());
                intent.putExtra("published", news.getPublished());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                finish();
            }
        }));

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);
        loadData();
    }

    private void loadData() {
        String url = Config.URL_DAUM_ITEM_NEWS_LIST + mCode;
        //Log.e(TAG, "url: " + url);
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(TAG, "response:\n" + response);
                parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseData("");
            }
        });

        BaseApplication.getInstance().addToRequestQueue(strReq, TAG);
    }

    private void parseData(String response) {
        mNewsList.clear();
        DaumNewsParser daumNewsParser = new DaumNewsParser();
        daumNewsParser.parseList(response, mNewsList, mCode, mName);

        renderData();
    }

    private void renderData() {
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_news_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_finish:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSwipeRight() {

    }

    @Override
    protected void onSwipeLeft() {

    }
}
