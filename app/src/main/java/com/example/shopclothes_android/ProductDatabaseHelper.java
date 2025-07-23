package com.example.shopclothes_android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class ProductDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "products_update_image.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_IMAGE_RES_ID = "imageResId";
    public static final String COL_IMAGE_URI = "imageUri";

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT, "
                + COL_PRICE + " REAL, "
                + COL_IMAGE_RES_ID + " INTEGER, "
                + COL_IMAGE_URI + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, product.getName());
        values.put(COL_PRICE, product.getPrice());
        values.put(COL_IMAGE_RES_ID, product.getImageResId());
        values.put(COL_IMAGE_URI, product.getImageUri());
        long id = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return id;
    }

    public int updateProduct(int id, Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, product.getName());
        values.put(COL_PRICE, product.getPrice());
        values.put(COL_IMAGE_RES_ID, product.getImageResId());
        values.put(COL_IMAGE_URI, product.getImageUri());
        int rows = db.update(TABLE_PRODUCTS, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE));
                int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IMAGE_RES_ID));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI));
                Product product = new Product(name, price, imageResId);
                product.setId(id);
                product.setImageUri(imageUri);
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return products;
    }
}
