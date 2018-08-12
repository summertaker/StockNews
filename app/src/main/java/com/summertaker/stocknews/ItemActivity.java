package com.summertaker.stocknews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.summertaker.stocknews.common.BaseActivity;
import com.summertaker.stocknews.common.BaseApplication;
import com.summertaker.stocknews.common.Config;
import com.summertaker.stocknews.common.DataManager;
import com.summertaker.stocknews.data.Item;
import com.summertaker.stocknews.util.Util;

public class ItemActivity extends BaseActivity {

    private String mCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        mContext = ItemActivity.this;
        initBaseActivity(mContext);
        initGesture();

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String published = intent.getStringExtra("published");

        // 뉴스 제목
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        // 뉴스 발행일
        TextView tvPublished = findViewById(R.id.tvPublished);
        tvPublished.setText(published);

        // 종목 코드 찾기
        for (Item item : BaseApplication.getInstance().getItemList()) {
            if (title.contains(item.getName())) {
                mCode = item.getCode();
                break;
            }
        }

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

                    // 가격
                    TextView tvPrice = findViewById(R.id.tvPrice);
                    String price = Config.NUMBER_FORMAT.format(item.getPrice());
                    price = String.format(getString(R.string.s_won), price);
                    tvPrice.setText(price);

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
                            // 카카오스탁 실행
                            Util.startKakaoStockDeepLink(mContext, mCode);
                        }
                    });
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
    protected void onSwipeRight() {
        finish();
    }

    @Override
    protected void onSwipeLeft() {

    }
}
