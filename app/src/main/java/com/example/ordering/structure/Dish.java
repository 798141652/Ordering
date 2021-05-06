package com.example.ordering.structure;

public class Dish {

    public String dishName,dishType,dishImage;
    public int shopID,dishID;
    public double dishPrice;

    public Dish(String dishName, String dishType, int shopID, int dishID, String dishImage, double dishPrice) {
        this.dishName = dishName;
        this.dishType = dishType;
        this.shopID = shopID;
        this.dishID = dishID;
        this.dishImage = dishImage;
        this.dishPrice = dishPrice;
    }

    public Dish(){};

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishType() {
        return dishType;
    }

    public void setDishType(String dishType) {
        this.dishType = dishType;
    }

    public int getShopID() {
        return shopID;
    }

    public void setShopID(int shopID) {
        this.shopID = shopID;
    }

    public int getDishID() {
        return dishID;
    }

    public void setDishID(int dishID) {
        this.dishID = dishID;
    }

    public String getDishImage() {
        return dishImage;
    }

    public void setDishImage(String dishImage) {
        this.dishImage = dishImage;
    }

    public double getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(double dishPrice) {
        this.dishPrice = dishPrice;
    }
}
