package com.summertaker.stocknews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.summertaker.stocknews.common.BaseActivity;
import com.summertaker.stocknews.common.DataManager;
import com.summertaker.stocknews.data.News;
import com.summertaker.stocknews.util.RecyclerTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private BreakingListAdapter mAdapter;
    private ArrayList<News> mNewsList;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mContext = MainActivity.this;
        initBaseActivity(mContext);

        mNewsList = new ArrayList<>();
        mAdapter = new BreakingListAdapter(mNewsList);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL)); // Divider
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(mContext, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position < mNewsList.size()) {
                    News news = mNewsList.get(position);
                    //Util.startKakaoStockDeepLink(mContext, breaking_detail.getCode());
                    Intent intent = new Intent(mContext, BreakingDetailActivity.class);
                    intent.putExtra("title", news.getTitle());
                    intent.putExtra("url", news.getUrl());
                    intent.putExtra("published", news.getPublished());
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //finish();
                //Item breaking_detail = mItems.get(position);
                //Util.startKakaoStockDeepLink(mContext, breaking_detail.getCode());
            }
        }));

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                //loadBreakingList();
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);
        loadItemList();
    }

    private void loadItemList() {
        mDataManager.setOnItemListLoaded(new DataManager.ItemListCallback() {
            @Override
            public void onParse(int count) {

            }

            @Override
            public void onLoad() {
                //Log.e(TAG, "ItemList = " + BaseApplication.getInstance().getItemList().size());
                loadNewsList();
            }
        });
        mDataManager.loadItemList();
    }

    private void loadNewsList() {
        //mDataManager.writeNewsList(new ArrayList<News>());
        mDataManager.setOnNewsListLoaded(new DataManager.NewsListCallback() {
            @Override
            public void onLoad(ArrayList<News> newList) {
                //Log.e(TAG, "newList.size() = " + newList.size());
                if (newList.size() > 0) {
                    mNewsList.clear();
                    mNewsList.addAll(newList);

                    /*
                    // 새로운 목록 추가하기
                    mNewsList.clear();
                    mNewsList.addAll(newList);

                    // 저장된 목록 추가하기
                    ArrayList<News> savedList = mDataManager.readNewsList();
                    long id = newList.size() + 1;
                    for (News newNews : newList) {
                        News news = null;
                        for (News savedNews : savedList) {
                            if (!newNews.getUrl().equals(savedNews.getUrl())) {
                                news = savedNews;
                                break;
                            }
                        }
                        if (news != null) {
                            //Log.e(TAG, "- Added: " + id + ". " + news.getTitle());
                            news.setId(id);
                            mNewsList.add(news);
                            id++;
                        }
                    }
                    mDataManager.writeNewsList(mNewsList);
                    */
                }

                renderData();
            }
        });
        mDataManager.loadNewsList();
    }

    private void renderData() {
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();

        //setActionBarTitleCount(mNewsList.size());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("M월 d일 (EEE) a H:mm:ss", Locale.KOREAN);
        String title = sdf.format(calendar.getTime());
        setActionBarTitle(title);

        /*
        if (mNewsListSize > 0 && mNewsListSize != mNewsList.size()) {
            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(1000);
            }
        }
        mNewsListSize = mNewsList.size();
        */

        //Toast.makeText(mContext, mNewsList.size() + " news loaded.", Toast.LENGTH_LONG).show();
        //Snackbar.make(mRecyclerView, mNewsList.size() + " news loaded.", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        loadNewsList();
                    }
                },
                5000);
    }

    private void goToTheTop() {
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (manager != null) {
            manager.scrollToPositionWithOffset(0, 0);
        }
    }

    protected void onToolbarClick() {
        goToTheTop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                goToTheTop();
                loadNewsList();
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
