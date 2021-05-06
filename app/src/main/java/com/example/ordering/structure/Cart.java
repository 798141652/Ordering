package com.example.ordering.structure;

public class Cart {

    public int cartID;
    public String orderID;
    public int cartUserID;
    public int cartShopID;
    public int cartDishID;

    public String cartDishName;
    public int cartDishNum;
    public Double cartDishPrice;

    public Double cartPrice;
    public String cartStatus;
    public String cartTime;

    public Cart() {
    }

    public int getCartID() {
        return cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public int getCartUserID() {
        return cartUserID;
    }

    public void setCartUserID(int cartUserID) {
        this.cartUserID = cartUserID;
    }

    public int getCartShopID() {
        return cartShopID;
    }

    public void setCartShopID(int cartShopID) {
        this.cartShopID = cartShopID;
    }

    public int getCartDishID() {
        return cartDishID;
    }

    public void setCartDishID(int cartDishID) {
        this.cartDishID = cartDishID;
    }

    public String getCartDishName() {
        return cartDishName;
    }

    public void setCartDishName(String cartDishName) {
        this.cartDishName = cartDishName;
    }

    public int getCartDishNum() {
        return cartDishNum;
    }

    public void setCartDishNum(int cartDishNum) {
        this.cartDishNum = cartDishNum;
    }

    public Double getCartDishPrice() {
        return cartDishPrice;
    }

    public void setCartDishPrice(Double cartDishPrice) {
        this.cartDishPrice = cartDishPrice;
    }

    public Double getCartPrice() {
        return cartPrice;
    }

    public void setCartPrice(Double cartPrice) {
        this.cartPrice = cartPrice;
    }

    public String getCartStatus() {
        return cartStatus;
    }

    public void setCartStatus(String cartStatus) {
        this.cartStatus = cartStatus;
    }

    public String getCartTime() {
        return cartTime;
    }

    public void setCartTime(String cartTime) {
        this.cartTime = cartTime;
    }
}
