package com.summertaker.stocknews.common;

import java.text.DecimalFormat;

public class Config {

    private final static String PACKAGE_NAME = "com.summertaker.stocknews";
    public final static String PREFERENCE_KEY = PACKAGE_NAME;
    public final static String PREFERENCE_NEWS_LIST = "news_list";

    public static DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###,###");
    public static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("+#,##0.00;-#");

    public static String NEWS_TEXT_HIGHLIGHT_FORMAT = "<font color='#D32F2F'>%s</font>"; // Red
    public static String NEWS_ITEM_NAME_HIGHLIGHT_FORMAT = "<font color='#1976D2'>%s</font>"; // Blue
    public static String NEWS_ITEM_NAME_HYPERLINK_FORMAT = "<a href='%s'>%s</a>"; // Blue

    public static String URL_DAUM_PRICE_KOSPI = "http://finance.daum.net/quote/all.daum?type=S&stype=P";
    public static String URL_DAUM_PRICE_KOSDAQ = "http://finance.daum.net/quote/all.daum?type=S&stype=Q";
    public static String URL_DAUM_DETAIL = "http://finance.daum.net/item/main.daum?code=";
    public static String URL_DAUM_ITEM_NEWS_LIST = "http://finance.daum.net/item/news.daum?code=";

    public static String URL_NAVER_NEWS_BREAKING = "https://finance.naver.com/news/news_list.nhn?mode=LSS2D&section_id=101&section_id2=258&page=";
}
