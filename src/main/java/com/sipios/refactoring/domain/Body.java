package com.sipios.refactoring.domain;

public class Body {

    private Item[] items;
    private String shopperType;

    public Body(Item[] items, String shopperType) {
        this.items = items;
        this.shopperType = shopperType;
    }

    public Body() {
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public String getShopperType() {
        return shopperType;
    }

    public void setShopperType(String shopperType) {
        this.shopperType = shopperType;
    }
}
