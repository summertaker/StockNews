package com.summertaker.stocknews.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.stocknews.parser.NaverNewsParser;
import com.summertaker.stocknews.data.Item;
import com.summertaker.stocknews.data.News;
import com.summertaker.stocknews.parser.DaumParser;
import com.summertaker.stocknews.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class DataManager {

    private String TAG;

    private Context mContext;
    private SharedPreferences mPreferences;
    private String mDateFormatString = "yyyy-MM-dd HH:mm:ss";
    private SimpleDateFormat mSimpleDateFormat;

    private boolean mIsDataOnLoading = false;
    private int mUrlLoadCount = 0;
    private ArrayList<String> mUrlList = new ArrayList<>();
    private ArrayList<Item> mItemList = new ArrayList<>();

    public DataManager() {
        TAG = getClass().getSimpleName();
    }

    public DataManager(Context context) {
        TAG = getClass().getSimpleName();
        mContext = context;
        mPreferences = context.getSharedPreferences(Config.PREFERENCE_KEY, Context.MODE_PRIVATE);
        mSimpleDateFormat = new SimpleDateFormat(mDateFormatString, Locale.getDefault());
    }

    /**
     * [네이버 금융 > 국내 > 전종목 시세] 가져오기 (실시간 가격)
     */
    public interface ItemListCallback {
        void onParse(int count);

        void onLoad();
    }

    private ItemListCallback mItemListCallback;

    public void setOnItemListLoaded(ItemListCallback callback) {
        mItemListCallback = callback;
    }

    public void loadItemList() {
        mUrlLoadCount = 0;
        mUrlList.clear();
        mUrlList.add(Config.URL_DAUM_PRICE_KOSPI);
        mUrlList.add(Config.URL_DAUM_PRICE_KOSDAQ);

        mItemList.clear();
        requestItemList();
    }

    private void requestItemList() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mUrlList.get(mUrlLoadCount), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseItemList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                parseItemList("");
            }
        });

        BaseApplication.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private void parseItemList(String response) {
        DaumParser daumParser = new DaumParser();
        daumParser.parseItemList(response, mItemList);

        mUrlLoadCount++;
        if (mUrlLoadCount < mUrlList.size()) {
            mItemListCallback.onParse(mUrlLoadCount);
            requestItemList();
        } else {
            //Log.e(TAG, "ItemPrice: mItems.size(): " + mItems.size());
            //writeCacheItems(Config.KEY_ITEM_PRICE, BaseApplication.getInstance().getItemPrices());

            // 뉴스에서 종목 이름을 강조하기 위해 글자 길이로 정렬 (SK하이닉스 > SK)
            for (Item item : mItemList) {
                item.setCharCount(item.getName().length());
            }
            Collections.sort(mItemList, new Comparator<Item>() {
                @Override
                public int compare(Item a, Item b) {
                    if (a.getCharCount() < b.getCharCount()) {
                        return 1;
                    } else if (a.getCharCount() > b.getCharCount()) {
                        return -1;
                    }
                    return 0;
                }
            });

            BaseApplication.getInstance().getItemList().clear();
            BaseApplication.getInstance().getItemList().addAll(mItemList);
            mItemListCallback.onLoad();
        }
    }

    /**
     * [다음 금융 > 종목 상세 정보] 로드하기
     */
    public interface ItemCallback {
        void onLoad(Item item);
    }

    private ItemCallback mItemCallback;

    public void setOnItemLoaded(ItemCallback callback) {
        mItemCallback = callback;
    }

    public void loadItem(String code) {
        requestItem(code);
    }

    private void requestItem(final String code) {
        String url = Config.URL_DAUM_DETAIL + code;
        //Log.e(mTag, "url: " + url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response:\n" + response);
                parseItem(response, code);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseItem("", code);
            }
        });

        BaseApplication.getInstance().addToRequestQueue(strReq, TAG);
    }

    private void parseItem(String response, String code) {
        DaumParser daumParser = new DaumParser();
        Item item = daumParser.parseItemDetail(response);
        item.setCode(code);
        mItemCallback.onLoad(item);
    }

    /**
     * [네이버 금융 > 뉴스 목록] 가져오기
     */
    public interface NewsListCallback {
        void onLoad(ArrayList<News> list);
    }

    private NewsListCallback mNewsListCallback;

    public void setOnNewsListLoaded(NewsListCallback callback) {
        mNewsListCallback = callback;
    }

    public void loadNewsList() {
        requestNewsList(Config.URL_NAVER_NEWS_BREAKING);
    }

    private void requestNewsList(String url) {
        //Log.e(TAG, "url: " + url);
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(TAG, "response:\n" + response);
                parseNewsList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseNewsList("");
            }
        });

        BaseApplication.getInstance().addToRequestQueue(strReq, TAG);
    }

    private void parseNewsList(String response) {
        ArrayList<News> list = new ArrayList<>();
        NaverNewsParser naverNewsParser = new NaverNewsParser();
        naverNewsParser.parseBreakingList(response, list);
        mNewsListCallback.onLoad(list);
    }

    public String readPreferences(String key) {
        return mPreferences.getString(key, "");
    }

    public void writePreferences(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 저장된 뉴스 목록 읽어오기
     */
    public ArrayList<News> readNewsList() {
        ArrayList<News> newsList = new ArrayList<>();
        String cacheData = readPreferences(Config.PREFERENCE_NEWS_LIST);
        if (!cacheData.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(cacheData);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    News news = new News();
                    //news.setId(Util.getInt(obj, "id"));
                    news.setTitle(Util.getString(obj, "title"));
                    news.setUrl(Util.getString(obj, "url"));
                    news.setPublished(Util.getString(obj, "published"));
                    //Log.e(TAG, news.getTitle());
                    newsList.add(news);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newsList;
    }

    /**
     * 뉴스 목록 로컬에 저장하기
     */
    public void writeNewsList(ArrayList<News> newsList) {
        try {
            JSONObject json = new JSONObject();
            JSONArray array = new JSONArray();
            for (News news : newsList) {
                JSONObject obj = new JSONObject();
                //obj.put("id", news.getId());
                obj.put("title", news.getTitle());
                obj.put("url", news.getUrl());
                obj.put("published", news.getPublished());
                //Log.e(TAG, portfolio.getTitle());
                array.put(obj);
            }
            json.put("data", array);
            writePreferences(Config.PREFERENCE_NEWS_LIST, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
