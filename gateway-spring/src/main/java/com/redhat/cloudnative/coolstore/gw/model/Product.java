package com.redhat.cloudnative.coolstore.gw.model;

public class Product {

    private String itemId;
    private String name;
    private String desc;
    private double price;
    private Inventory availability;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Inventory getAvailability() {
        return availability;
    }

    public void setAvailability(Inventory availability) {
        this.availability = availability;
    }
}
