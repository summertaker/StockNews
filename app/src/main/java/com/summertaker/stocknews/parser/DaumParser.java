package com.summertaker.stocknews.parser;

import com.summertaker.stocknews.common.BaseParser;
import com.summertaker.stocknews.data.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class DaumParser extends BaseParser {

    public void parseItemList(String response, ArrayList<Item> items) {
        if (response == null || response.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(response);

        Elements tables = doc.getElementsByTag("table");
        for (Element table : tables) {

            String className = table.attr("class");
            if (!"gTable clr".equals(className)) {
                continue;
            }

            Elements trs = table.getElementsByTag("tr");
            for (Element tr : trs) {

                Elements tds = tr.getElementsByTag("td");
                if (tds.size() != 6) {
                    continue;
                }

                parseItemRow(items, tds.get(0), tds.get(1), tds.get(2));
                parseItemRow(items, tds.get(3), tds.get(4), tds.get(5));
            }
        }
    }

    private void parseItemRow(ArrayList<Item> items, Element td1, Element td2, Element td3) {
        Item item = new Item();

        Element el = td1.getElementsByTag("a").get(0);
        String href = el.attr("href");
        String code = getCodeFromUrl(href);
        item.setCode(code);

        String name = el.text();
        item.setName(name);

        String temp;

        temp = td2.text();
        temp = temp.replace(",", "");
        int price = Integer.parseInt(temp);
        item.setPrice(price);

        temp = td3.text();
        temp = temp.replace(",", "");
        temp = temp.replace("%", "");
        float rof = Float.valueOf(temp);
        item.setRof(rof);

        //Log.e(TAG, name + " " + price + " / " + rof);

        items.add(item);
    }

    public Item parseItemDetail(String response) {
        Item item = new Item();

        if (response == null || response.isEmpty()) {
            return item;
        }

        Document doc = Jsoup.parse(response);

        // 제목
        Elements h2s = doc.getElementsByTag("h2");
        for (Element h2 : h2s) {
            String attr = h2.attr("onclick");
            //Log.e(TAG, "attr: " + attr);

            if (attr.contains("GoPage(")) {
                String name = h2.text();
                item.setName(name);
                break;
            }
        }

        // 상세
        Elements uls = doc.getElementsByTag("ul");
        for (Element ul : uls) {
            String attr = ul.attr("class");
            //Log.e(TAG, "attr: " + attr);

            if ("list_stockrate".equals(attr)) {

                int price;
                int pof;
                float rof;
                String temp;

                Elements lis = ul.getElementsByTag("li");

                temp = lis.get(0).text();
                temp = temp.replace(",", "");
                price = Integer.valueOf(temp);

                temp = lis.get(1).text();
                temp = temp.replace(",", "");
                pof = Integer.valueOf(temp);

                temp = lis.get(2).text();
                temp = temp.replace(",", "");
                temp = temp.replace("%", "");
                temp = temp.replace("％", "");
                rof = Float.valueOf(temp);

                //Log.e(TAG, price + " " + pof + " " + rof);

                item.setPrice(price);
                item.setPof(pof);
                item.setRof(rof);
            }
        }

        return item;
    }
}
