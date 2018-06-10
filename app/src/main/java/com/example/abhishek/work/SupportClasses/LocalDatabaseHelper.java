package com.example.abhishek.work.SupportClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.abhishek.work.Model.ItemData;

import java.util.ArrayList;
import java.util.List;

public class LocalDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "retailer";
    private static final int DATABASE_VERSION = 1;


    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ItemData.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemData.TABLE_NAME);
    }

    public void insertItem(ItemData itemData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemData.COLUMN_PRODUCT_ID, itemData.getProductID());
        values.put(ItemData.COLUMN_PRICE, itemData.getPrice());
        values.put(ItemData.COLUMN_DESCRIPTION, itemData.getDescription());
        values.put(ItemData.COLUMN_PHOTO, itemData.getPhoto());
        values.put(ItemData.COLUMN_AVAILABILITY, itemData.getAvailability());
        values.put(ItemData.COLUMN_STAR, itemData.getStar());

        long id = db.insert(ItemData.TABLE_NAME, null, values);
        db.close();
    }

    public ItemData getProduct(int productID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ItemData.TABLE_NAME
                , new String[]{ItemData.COLUMN_PRODUCT_ID
                        , ItemData.COLUMN_PRICE, ItemData.COLUMN_DESCRIPTION, ItemData.COLUMN_PHOTO
                        , ItemData.COLUMN_AVAILABILITY, ItemData.COLUMN_STAR}
                , ItemData.COLUMN_PRODUCT_ID + "=?"
                , new String[]{String.valueOf(productID)}
                , null
                , null
                , null
                , null);

        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();

            Log.e("DBhelper get_products",cursor.toString());

            ItemData itemData = new ItemData();
            itemData.setProductID(cursor.getInt(cursor.getColumnIndex(ItemData.COLUMN_PRODUCT_ID)));
            itemData.setPrice(cursor.getInt(cursor.getColumnIndex(ItemData.COLUMN_PRICE)));
            itemData.setDescription(cursor.getString(cursor.getColumnIndex(ItemData.COLUMN_DESCRIPTION)));
            itemData.setPhoto(cursor.getString(cursor.getColumnIndex(ItemData.COLUMN_PHOTO)));
            itemData.setAvailability(cursor.getInt(cursor.getColumnIndex(ItemData.COLUMN_AVAILABILITY)));
            itemData.setStar(cursor.getInt(cursor.getColumnIndex(ItemData.COLUMN_STAR)));
            cursor.close();

            return itemData;
        }else {
            ItemData itemData = new ItemData();
            itemData.setProductID(-1);
            return itemData;
        }

    }

    public List<ItemData> getAllProducts() {
        List<ItemData> productsList = new ArrayList<>();

        String query = "SELECT * FROM " + ItemData.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ItemData itemData = new ItemData();
                itemData.setProductID(cursor.getInt(cursor.getColumnIndex(ItemData.COLUMN_PRODUCT_ID)));
                itemData.setPrice(cursor.getInt(cursor.getColumnIndex(ItemData.COLUMN_PRICE)));
                itemData.setDescription(cursor.getString(cursor.getColumnIndex(ItemData.COLUMN_DESCRIPTION)));
                itemData.setPhoto(cursor.getString(cursor.getColumnIndex(ItemData.COLUMN_PHOTO)));
                itemData.setAvailability(cursor.getInt(cursor.getColumnIndex(ItemData.COLUMN_AVAILABILITY)));
                itemData.setStar(cursor.getInt(cursor.getColumnIndex(ItemData.COLUMN_STAR)));

                productsList.add(itemData);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return productsList;
    }

    public int getProductesCount() {

        String query = "SELECT * FROM " + ItemData.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int updateProduct(ItemData itemData) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemData.COLUMN_PRICE, itemData.getPrice());
        values.put(ItemData.COLUMN_STAR, itemData.getStar());
        values.put(ItemData.COLUMN_NAME, itemData.getName());
        values.put(ItemData.COLUMN_SELLING_PRICE, itemData.getSellingPrice());
        values.put(ItemData.COLUMN_AVAILABILITY, itemData.getAvailability());
        values.put(ItemData.COLUMN_DESCRIPTION, itemData.getDescription());
        values.put(ItemData.COLUMN_PHOTO, itemData.getPhoto());
        values.put(ItemData.COLUMN_COMMENT, itemData.getComment());

        int i = db.update(ItemData.TABLE_NAME, values, ItemData.COLUMN_PRODUCT_ID + " = ? "
                , new String[]{String.valueOf(itemData.getProductID())});

        return i;
    }

    public void deleteProduct(ItemData itemData){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ItemData.TABLE_NAME,ItemData.COLUMN_PRODUCT_ID + " = ?"
                ,new String[]{String.valueOf(itemData.getProductID())});
        db.close();
    }
}
