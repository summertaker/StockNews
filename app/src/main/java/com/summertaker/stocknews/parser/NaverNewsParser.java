package com.summertaker.stocknews.parser;

import com.summertaker.stocknews.common.BaseParser;
import com.summertaker.stocknews.common.Config;
import com.summertaker.stocknews.data.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class NaverNewsParser extends BaseParser {

    /**
     * 속보 목록 파싱하기
     */
    public void parseBreakingList(String response, ArrayList<News> list) {
        if (response == null || response.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(response);
        Element ul = doc.select(".realtimeNewsList").first();

        ArrayList<News> newsList = new ArrayList<>();
        for (Element li : ul.select("li")) {
            for (Element el : li.select(".articleSubject")) {
                String title;
                String url;

                title = el.text();
                //title = title.replaceAll("\\[.+\\]\\s?", "");
                //title = title.replaceAll("\\(.+\\)\\s?", "");
                //title = title.replaceAll("\\(\\d+\\)", "");
                //title = title.replaceAll("<.+>\\s?", "");

                url = el.attr("href");
                url = "https://finance.naver.com" + url;

                //Log.e(TAG, id + ". " + title); // + " " + url);

                News news = new News();
                news.setTitle(title);
                news.setUrl(url);
                newsList.add(news);
            }
        }

        int count = 0;
        for (Element li : ul.select("li")) {
            for (Element el : li.select(".wdate")) {
                String published;
                published = el.text();

                //Log.e(TAG, i + ". " + published + " " + elapsed);

                newsList.get(count).setPublished(published);
                count++;
            }
        }

        ArrayList<String> excludeList = new ArrayList<>();
        excludeList.add("코스닥");
        excludeList.add("코스피");
        excludeList.add("출발");

        ArrayList<String> includeList = new ArrayList<>();
        includeList.add("강세");
        includeList.add("급등");
        includeList.add("상승");
        //includeList.add("↑");

        long id = 1;
        for (News news : newsList) {
            // 제외
            boolean exclude = false;
            for (String word : excludeList) {
                if (news.getTitle().contains(word)) {
                    exclude = true;
                    break;
                }
            }
            if (exclude) continue;

            // 포함
            String title = news.getTitle();
            boolean include = false;
            for (String word : includeList) {
                if (news.getTitle().contains(word)) {
                    include = true;
                    title = title.replace(word, String.format(Config.NEWS_TEXT_HIGHLIGHT_FORMAT, word));
                    //break;
                }
            }
            if (!include) continue;

            news.setId(id);
            news.setTitle(title);
            list.add(news);
            id++;
        }
    }
}
