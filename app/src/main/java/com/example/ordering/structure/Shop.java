package com.example.ordering.structure;

public class Shop {
    public String shopName,shopLocation,shopBrief,shopImage;
    public int shopID;

    public Shop( int shopID,String shopName, String shopImage, String shopLocation, String shopBrief) {
        this.shopName = shopName;
        this.shopImage = shopImage;
        this.shopLocation = shopLocation;
        this.shopBrief = shopBrief;
        this.shopID = shopID;
    }

    public int getShopID() {
        return shopID;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public void setShopID(int shopID) {
        this.shopID = shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopLocation() {
        return shopLocation;
    }

    public void setShopLocation(String shopLocation) {
        this.shopLocation = shopLocation;
    }

    public String getShopBrief() {
        return shopBrief;
    }

    public void setShopBrief(String shopBrief) {
        this.shopBrief = shopBrief;
    }

}
