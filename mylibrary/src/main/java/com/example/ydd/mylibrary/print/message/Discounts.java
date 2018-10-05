package com.example.ydd.mylibrary.print.message;

/**
 *
 * 优惠和支付类型用一个
 */
public class Discounts {
    private float discounts;//优惠
    private int type;//类型
    private float subtotal;//打折

    public float getDiscounts() {
        return discounts;
    }

    public void setDiscounts(float discounts) {
        this.discounts = discounts;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }
}
