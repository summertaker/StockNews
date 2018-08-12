package com.summertaker.stocknews.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseParser {

    protected String TAG;

    public BaseParser() {
        TAG = this.getClass().getSimpleName();
    }

    protected String getCodeFromUrl(String url) {
        String code = "";
        Pattern pattern = Pattern.compile("code=(\\d+)");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            code = matcher.group(1);
        }
        return code;
    }
}
