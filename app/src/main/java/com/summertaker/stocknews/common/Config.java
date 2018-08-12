package com.summertaker.stocknews.common;

import java.text.DecimalFormat;

public class Config {

    private final static String PACKAGE_NAME = "com.summertaker.stocknews";
    public final static String PREFERENCE_KEY = PACKAGE_NAME;
    public final static String PREFERENCE_NEWS_LIST = "news_list";

    public final static String RECYCLERVIEW_LIST_STATE = "recyclerview_list_state";

    public static DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###,###");

    public static String URL_DAUM_PRICE_KOSPI = "http://finance.daum.net/quote/all.daum?type=S&stype=P";
    public static String URL_DAUM_PRICE_KOSDAQ = "http://finance.daum.net/quote/all.daum?type=S&stype=Q";
    public static String URL_DAUM_DETAIL = "http://finance.daum.net/item/main.daum?code=";

    public static String URL_NAVER_NEWS_BREAKING = "https://finance.naver.com/news/news_list.nhn?mode=LSS2D&section_id=101&section_id2=258&page=";
}
