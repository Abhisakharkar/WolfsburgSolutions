package com.example.abhishek.work.Model;

public class ItemData {

    private int productID;
    private String name;
    private String description;
    private String comment;
    private String photo;
    private double price;
    private int availability,star;
    private double sellingPrice;
    private int attribute_set_id;

    public static final String TABLE_NAME = "items";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_AVAILABILITY = "availability";
    public static final String COLUMN_SELLING_PRICE = "selling_price";
    public static final String COLUMN_STAR = "star";
    public static final String COLUMN_ATTRIBUTE_SET_ID = "attriibute_set_id";
    public static final String COLUMN_COMMENT = "comment";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_PRODUCT_ID + " INTEGER(11) PRIMARY KEY,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_PRICE + " DOUBLE,"
                    + COLUMN_SELLING_PRICE + " DOUBLE,"
                    + COLUMN_DESCRIPTION + " TEXT,"
                    + COLUMN_COMMENT + " TEXT,"
                    + COLUMN_ATTRIBUTE_SET_ID + "INTEGER,"
                    + COLUMN_PHOTO + " TEXT,"
                    + COLUMN_AVAILABILITY + " TEXT,"
                    + COLUMN_STAR + " INTEGER"
                    + ")";

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
