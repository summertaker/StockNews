package com.summertaker.stocknews.parser;

import android.util.Log;

import com.summertaker.stocknews.R;
import com.summertaker.stocknews.common.BaseParser;
import com.summertaker.stocknews.common.Config;
import com.summertaker.stocknews.data.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DaumNewsParser extends BaseParser {

    public void parseList(String response, ArrayList<News> list, String code, String name) {
        if (response == null || response.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("itemNewsList");
        if (root == null) {
            return;
        }
        Element ul = root.getElementsByTag("ul").first();
        if (ul == null) {
            return;
        }

        SimpleDateFormat inFormat = new SimpleDateFormat("yy.MM.dd HH:mm", Locale.getDefault());
        SimpleDateFormat outFormat = new SimpleDateFormat("M월 d일 (EEE) a H:mm", Locale.KOREAN);

        long id = 1;
        for (Element li : ul.select("li")) {
            String title;
            String url;
            String summary;
            String published;

            Element el;
            el = li.select("strong > a").first();
            title = el.text();
            title = title.replaceAll("\\[.+\\]\\s?", "");
            title = title.replaceAll("\\(.+\\)\\s?", "");
            //title = title.replaceAll("\\(\\d+\\)", "");
            title = title.replaceAll("<.+>\\s?", "");

            title = title.replace(name, String.format(Config.NEWS_ITEM_NAME_HIGHLIGHT_FORMAT, name));

            //if (isInExcludeList(Config.KEY_WORD_CATEGORY_DAUM_EXCLUDE, title)) { // 제외 단어
            //    continue;
            //}

            //title = highlightText(Config.KEY_WORD_CATEGORY_DAUM_INCLUDE, title); // 단어 강조

            url = el.attr("href");
            url = "http://m.finance.daum.net/" + url;

            el = li.select("span.datetime").first();
            published = el.text();
            try {
                Date date = inFormat.parse(published);
                published = outFormat.format(date);
                //Log.e(TAG, "published: " + published);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            el = li.select("p > a").first();
            summary = el.text();
            summary = summary.replaceAll("\\[.+\\]\\s?", "");
            summary = summary.replaceAll("\\(.+\\)\\s?", "");
            summary = summary.replaceAll("<.+>\\s?", "");

            // 제목과 내용에 종목이름이 없으면 제외
            if (!title.contains(name)) { // && !summary.contains(name)) {
                continue;
            }

            //Log.e(TAG, title + " " + published);

            News news = new News();
            news.setId(id);
            news.setTitle(title);
            news.setUrl(url);
            news.setPublished(published);
            list.add(news);
            id++;
        }
    }

    public void parseDetail(String response, News news, String name) {
        if (response == null || response.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(response);

        // 내용
        Element con = doc.getElementById("dmcfContents");
        if (con == null) return;

        Element sec = con.getElementsByTag("section").get(0);
        if (sec == null) return;

        // 불필요한 태그 제거
        sec.select("figure").remove();
        sec.select("img").remove();

        // 불필요한 단어 제거
        String html = sec.html();
        html = html.replaceAll("【.*】", "");
        html = html.replaceAll("\\[.*\\]", "");
        html = html.replaceAll("^.+\">", "");

        /*
        // 광고 단어 이후 내용 제거
        for (Word word : BaseApplication.getInstance().getWords()) {
            if (word.getCategory().equals(Config.KEY_WORD_CATEGORY_DAUM_AD)) {
                String value = word.getValue();
                value = value.replaceAll("\\(", "\\\\(");
                value = value.replaceAll("\\)", "\\\\)");
                html = html.split(value)[0];
            }
        }
        */

        // 줄 단위 정리
        String[] rows = html.split("</p>");
        ArrayList<String> list = new ArrayList<>();
        for (String row : rows) {
            String text = row.replaceAll("<[^>]*>", "").trim();

            if (text.isEmpty()) continue;
            //Log.e(TAG, text);

            list.add(text);
        }

        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (String text : list) {
            if (count < list.size()) {
                text = "<p>" + text + "</p>";
            }
            //Log.e(TAG, text);
            //content += text;
            sb.append(text);
            count++;
        }

        String content = sb.toString();
        content = content.replaceAll("\\[.+\\]\\s?", "");
        content = content.replaceAll("\\(\\d+\\)", "");

        // 단어 강조
        //content = highlightText(Config.KEY_WORD_CATEGORY_DAUM_INCLUDE, content);

        // 종목 이름 강조
        content = content.replace(name, String.format(Config.NEWS_TEXT_HIGHLIGHT_FORMAT, name));

        news.setContent(content);
    }
}

