package com.summertaker.stocknews;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.summertaker.stocknews.common.BaseActivity;
import com.summertaker.stocknews.common.BaseApplication;
import com.summertaker.stocknews.common.Config;
import com.summertaker.stocknews.common.DataManager;
import com.summertaker.stocknews.data.Item;
import com.summertaker.stocknews.util.Util;

public class BreakingDetailActivity extends BaseActivity {

    private String mCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breaking_detail_activity);

        mContext = BreakingDetailActivity.this;
        initBaseActivity(mContext);
        initGesture();

        Intent intent = getIntent();

        // 뉴스 제목
        String title = intent.getStringExtra("title");
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);

        // 뉴스 발행일
        String published = intent.getStringExtra("published");
        TextView tvPublished = findViewById(R.id.tvPublished);
        tvPublished.setText(published);

        // 종목 코드 찾기
        for (Item item : BaseApplication.getInstance().getItemList()) {
            if (title.contains(item.getName())) {
                mCode = item.getCode();
                break;
            }
        }

        LinearLayout loContent = findViewById(R.id.loContent);
        loContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                finish();
                return false;
            }
        });

        if (mCode != null) {
            mDataManager.setOnItemLoaded(new DataManager.ItemCallback() {
                @Override
                public void onLoad(Item item) {
                    LinearLayout loProgressbar = findViewById(R.id.loProgressbar);
                    loProgressbar.setVisibility(View.GONE);

                    LinearLayout loData = findViewById(R.id.loData);
                    loData.setVisibility(View.VISIBLE);

                    // 이름
                    TextView tvName = findViewById(R.id.tvName);
                    tvName.setText(item.getName());
                    setActionBarTitle(item.getName());

                    // 가격
                    TextView tvPrice = findViewById(R.id.tvPrice);
                    String price = Config.NUMBER_FORMAT.format(item.getPrice());
                    price = String.format(getString(R.string.s_won), price);
                    tvPrice.setText(price);

                    // 등락률
                    TextView tvRof = findViewById(R.id.tvRof);
                    String rof = Config.DECIMAL_FORMAT.format(item.getRof()) + "%";
                    tvRof.setText(rof);
                    if (item.getRof() < 0) {
                        tvRof.setTextColor(getColor(R.color.primary));
                    } else if (item.getRof() > 0) {
                        tvRof.setTextColor(getColor(R.color.danger));
                    } else {
                        tvRof.setTextColor(getColor(R.color.ink));
                    }

                    // 매수량
                    TextView tvVolume = findViewById(R.id.tvVolume);
                    int volume = 1000000 / item.getPrice();
                    String volumeText = Config.NUMBER_FORMAT.format(volume);
                    volumeText = String.format(getString(R.string.buy_volume_s_stock), volumeText);
                    tvVolume.setText(volumeText);

                    // 매수 버튼
                    Button btnBuy = findViewById(R.id.btnBuy);
                    btnBuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Util.startKakaoStockDeepLink(mContext, mCode);
                        }
                    });

                    // 뉴스 버튼
                    final String name = item.getName();
                    Button btnNews = findViewById(R.id.btnNews);
                    btnNews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent newsIntent = new Intent(mContext, ItemNewsListActivity.class);
                            newsIntent.putExtra("code", mCode);
                            newsIntent.putExtra("name", name);
                            startActivity(newsIntent);
                        }
                    });

                    // 일 차트
                    long millis = System.currentTimeMillis();
                    String dayChartUrl = "https://ssl.pstatic.net/imgfinance/chart/mobile/mini/" + mCode + "_end_up_tablet.png?" + millis;
                    ImageView ivDayChart = findViewById(R.id.ivDayChart);
                    Glide.with(mContext).load(dayChartUrl).apply(new RequestOptions()).into(ivDayChart);

                    // 주 차트
                    //String weekChartUrl = "https://ssl.pstatic.net/imgfinance/chart/item/area/week/" + mCode + ".png?sidcode=" + millis;
                    //ImageView ivWeekChart = findViewById(R.id.ivWeekChart);
                    //Glide.with(mContext).load(weekChartUrl).apply(new RequestOptions()).into(ivWeekChart);

                    // 주봉 차트
                    //String weekCandleChartUrl = "https://fn-chart.dunamu.com/images/kr/candle/w/A" + mCode + ".png?" + millis;
                    //ImageView ivWeekCandleChart = findViewById(R.id.ivMonthCandleChart);
                    //Glide.with(mContext).load(weekCandleChartUrl).apply(new RequestOptions()).into(ivWeekCandleChart);

                    // 월봉 차트
                    //String yearCandleChartUrl = "https://fn-chart.dunamu.com/images/kr/candle/m/A" + mCode + ".png?" + millis;
                    //ImageView ivYearCandleChart = findViewById(R.id.ivYearCandleChart);
                    //Glide.with(mContext).load(yearCandleChartUrl).apply(new RequestOptions()).into(ivYearCandleChart);
                }
            });
            mDataManager.loadItem(mCode);
        } else {
            LinearLayout loProgressbar = findViewById(R.id.loProgressbar);
            loProgressbar.setVisibility(View.GONE);

            LinearLayout loNoData = findViewById(R.id.loNoData);
            loNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.breaking_detail, menu);
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
        finish();
    }

    @Override
    protected void onSwipeLeft() {

    }
}
