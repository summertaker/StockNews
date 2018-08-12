package com.summertaker.stocknews.common;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.summertaker.stocknews.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG;

    protected Context mContext;
    protected Resources mResources;
    protected DataManager mDataManager;

    protected Toolbar mToolbar;
    protected String mActivityTitle;

    protected LinearLayout mLoProgress;
    protected ProgressBar mPbProgressBar;
    protected LinearLayout mLoContent;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;

    public BaseActivity() {
        TAG = getClass().getSimpleName();
    }

    protected void initBaseActivity(Context context) {
        mContext = context;
        mResources = context.getResources();
        mDataManager = new DataManager(mContext);

        //mSharedPreferences = getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);
        //mSharedEditor = mSharedPreferences.edit();

        /*
        if (!TAG.equals("MainActivity")) {
            // https://stackoverflow.com/questions/22192291/how-to-change-the-status-bar-color-in-android
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            //mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        finish();
            //    }
            //});
        }
        */

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolbarClick();
            }
        });
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && actionBar.getTitle() != null) {
            mActivityTitle = actionBar.getTitle().toString();
        }
        if (!TAG.equals("MainActivity")) {
            if (actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    protected void onToolbarClick() {

    }

    protected void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    protected void setActionBarTitleCount(int count) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String title = mActivityTitle;
            if (count > 0) {
                title = title + " (" + Config.NUMBER_FORMAT.format(count) + ")";
            }
            actionBar.setTitle(title);
        }
    }

    /*
    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        final int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                String code = span.getURL();
                //Toast.makeText(mContext, span.getURL(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("code", code);
                startActivity(intent);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }
    */

    // https://stackoverflow.com/questions/12418279/android-textview-with-clickable-links-how-to-capture-clicks
    protected void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        //URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        //for (URLSpan span : urls) {
        //    makeLinkClickable(strBuilder, span);
        //}
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    protected void initGesture() {
        gestureDetector = new GestureDetector(this, new SwipeDetector());
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.getInstance().cancelPendingRequests(TAG);
    }

    public class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Check movement along the Y-axis. If it exceeds SWIPE_MAX_OFF_PATH,
            // then dismiss the swipe.
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                return false;
            }

            //toast( "start = "+String.valueOf( e1.getX() )+" | end = "+String.valueOf( e2.getX() )  );
            //from left to right
            if (e2.getX() > e1.getX()) {
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    onSwipeRight();
                    return true;
                }
            }

            if (e1.getX() > e2.getX()) {
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    onSwipeLeft();
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TouchEvent dispatcher.
        if (gestureDetector != null) {
            if (gestureDetector.onTouchEvent(ev))
                // If the gestureDetector handles the event, a swipe has been
                // executed and no more needs to be done.
                return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector != null) {
            return gestureDetector.onTouchEvent(event);
        } else {
            return true;
        }
    }

    protected abstract void onSwipeRight();

    protected abstract void onSwipeLeft();
}

