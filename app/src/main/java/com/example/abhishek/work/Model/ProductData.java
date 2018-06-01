package com.example.abhishek.work.Model;

public class ProductData {
    private String name,desc,comment,photo;
    private int productID,attribute_set_id;
    private double price;
    private double sellingPrice;
    private boolean star,avail;

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getAttribute_set_id() {
        return attribute_set_id;
    }

    public void setAttribute_set_id(int attribute_set_id) {
        this.attribute_set_id = attribute_set_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    public boolean isAvail() {
        return avail;
    }

    public void setAvail(boolean avail) {
        this.avail = avail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
