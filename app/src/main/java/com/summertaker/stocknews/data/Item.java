package com.summertaker.stocknews.data;

public class Item {
    private long id;
    private String code;
    private String name;
    private int price;
    private int pof;   // 전일비 Price of Fluctuation
    private float rof; // 등락률 Rate of Fluctuation

    private int charCount = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPof() {
        return pof;
    }

    public void setPof(int pof) {
        this.pof = pof;
    }

    public float getRof() {
        return rof;
    }

    public void setRof(float rof) {
        this.rof = rof;
    }

    public int getCharCount() {
        return charCount;
    }

    public void setCharCount(int charCount) {
        this.charCount = charCount;
    }
}
