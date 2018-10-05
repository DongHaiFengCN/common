package com.example.ydd.mylibrary.print.message;

import java.util.List;

public class PrintData {

    private String serNum;//流水号
    private String areaName;//区域名字
    private String tableName;//桌位名字
    private int currentPersons;//桌位人数
    private List<Dish> dishList;//所有的菜品
    private String description;//备注
    private String employeeName;//人员
    private Payment payment;//支付
    public String getSerNum() {
        return serNum;
    }

    public void setSerNum(String serNum) {
        this.serNum = serNum;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getCurrentPersons() {
        return currentPersons;
    }

    public void setCurrentPersons(int currentPersons) {
        this.currentPersons = currentPersons;
    }

    public List<Dish> getDishList() {
        return dishList;
    }

    public void setDishList(List<Dish> dishList) {
        this.dishList = dishList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
