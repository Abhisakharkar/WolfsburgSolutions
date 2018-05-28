package com.example.abhishek.work.Model;

public class ItemData {

    private String retailerID, productID, description, photo;
    private float price;
    private int availability,star;

    public static final String TABLE_NAME = "items";
    public static final String COLUMN_RETAILER_ID = "retailer_id";
    public static final String COLUMN_PRODUCT_ID = "retailer_id";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_AVAILABILITY = "availability";
    public static final String COLUMN_STAR = "star";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_RETAILER_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_PRODUCT_ID + " INTEGER(11),"
                    + COLUMN_PRICE + " FLOAT,"
                    + COLUMN_DESCRIPTION + " TEXT,"
                    + COLUMN_PHOTO + " TEXT,"
                    + COLUMN_AVAILABILITY + " TEXT,"
                    + COLUMN_STAR + " INTEGER,"
                    + ")";

    public String getProductID() {

        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
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

    public String getRetailerID() {
        return retailerID;
    }

    public void setRetailerID(String retailerID) {
        this.retailerID = retailerID;
    }
}
