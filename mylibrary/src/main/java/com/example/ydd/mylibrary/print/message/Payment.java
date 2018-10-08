package com.example.ydd.mylibrary.print.message;

import java.util.List;

public class Payment {
    private float totalPrice;//总计
    private List<Discounts> discountsList;//活动优惠
    private List<Discounts> payDetailList;//支付方式
    private float wipeZero;//抹零
    private float receivable;//应收
    private boolean isPay;//是否是结账

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Discounts> getDiscountsList() {
        return discountsList;
    }

    public void setDiscountsList(List<Discounts> discountsList) {
        this.discountsList = discountsList;
    }

    public List<Discounts> getPayDetailList() {
        return payDetailList;
    }

    public void setPayDetailList(List<Discounts> payDetailList) {
        this.payDetailList = payDetailList;
    }

    public float getWipeZero() {
        return wipeZero;
    }

    public void setWipeZero(float wipeZero) {
        this.wipeZero = wipeZero;
    }

    public float getReceivable() {
        return receivable;
    }

    public void setReceivable(float receivable) {
        this.receivable = receivable;
    }

    public boolean isPay() {
        return isPay;
    }

    public void setPay(boolean pay) {
        isPay = pay;
    }


}
