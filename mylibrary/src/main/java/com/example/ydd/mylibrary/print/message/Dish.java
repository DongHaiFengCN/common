package com.example.ydd.mylibrary.print.message;

import java.util.List;

public class Dish {

    private  String name;//菜品名字
    private float count;//数量
    private int goodsType;//goods状态(赠，退，正常)
    private String tasteName;//口味
    private boolean isPromotion;//是否是套餐
    private List<String> promotionDishList;//套餐下的菜品名字
    private String description;//菜品备注
    private float price;//单价
    private int goodsAlter;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public int getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(int goodsType) {
        this.goodsType = goodsType;
    }

    public String getTasteName() {
        return tasteName;
    }

    public void setTasteName(String tasteName) {
        this.tasteName = tasteName;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion) {
        isPromotion = promotion;
    }

    public List<String> getPromotionDishList() {
        return promotionDishList;
    }

    public void setPromotionDishList(List<String> promotionDishList) {
        this.promotionDishList = promotionDishList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getGoodsAlter() {
        return goodsAlter;
    }

    public void setGoodsAlter(int goodsAlter) {
        this.goodsAlter = goodsAlter;
    }
}
