package com.summertaker.stocknews;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.stocknews.common.BaseActivity;
import com.summertaker.stocknews.common.BaseApplication;
import com.summertaker.stocknews.common.Config;
import com.summertaker.stocknews.data.News;
import com.summertaker.stocknews.parser.DaumNewsParser;

public class ItemNewsDetailActivity extends BaseActivity {

    private String mName;
    private News mNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_news_detail_activity);

        mContext = ItemNewsDetailActivity.this;
        initBaseActivity(mContext);

        Intent intent = getIntent();

        // 종목 이름
        mName = intent.getStringExtra("name");
        setActionBarTitle(mName);

        // 제목
        String title = intent.getStringExtra("title");
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        // 발행일
        String published = intent.getStringExtra("published");
        TextView tvPublished = findViewById(R.id.tvPublished);
        tvPublished.setText(published);

        // 뉴스 URL
        mNews = new News();
        mNews.setUrl(intent.getStringExtra("url"));

        loadData();
    }

    private void loadData() {
        StringRequest strReq = new StringRequest(Request.Method.GET, mNews.getUrl(), new Response.Listener<String>() {
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
        DaumNewsParser daumNewsParser = new DaumNewsParser();
        daumNewsParser.parseDetail(response, mNews, mName);
        renderData();
    }

    private void renderData() {
        // 내용
        TextView tvContent = findViewById(R.id.tvContent);
        String content = mNews.getContent();
        //content = content.replace(mItem.getName(), String.format(Config.NEWS_ITEM_NAME_HYPERLINK_FORMAT, mItem.getCode(), mItem.getName()));
        //tvContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
        setTextViewHTML(tvContent, content);
        tvContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                finish();
                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_news_detail, menu);
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
