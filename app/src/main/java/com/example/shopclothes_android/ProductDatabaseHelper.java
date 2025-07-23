package com.example.shopclothes_android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import com.example.shopclothes_android.Order;
import com.example.shopclothes_android.Product;

public class ProductDatabaseHelper extends SQLiteOpenHelper {
    // Lấy danh sách order theo userId (uuid)
    public List<Order> getOrdersByUserId(String userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("orders", null, "user_id=?", new String[]{userId}, null, null, "created_date DESC");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String userIdDb = cursor.getString(cursor.getColumnIndexOrThrow("user_id"));
                String productsStr = cursor.getString(cursor.getColumnIndexOrThrow("products"));
                double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
                String createdDate = cursor.getString(cursor.getColumnIndexOrThrow("created_date"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                List<Product> products = deserializeProducts(productsStr);
                Order order = new Order(userIdDb, products, totalPrice, createdDate, status);
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orders;
    }

    // Giải mã danh sách sản phẩm từ chuỗi lưu trong DB
    private List<Product> deserializeProducts(String productsStr) {
        List<Product> products = new ArrayList<>();
        if (productsStr == null || productsStr.isEmpty()) return products;
        String[] ids = productsStr.split(",");
        for (String idStr : ids) {
            try {
                int id = Integer.parseInt(idStr.trim());
                Product p = getProductById(id);
                if (p != null) products.add(p);
            } catch (Exception ignored) {}
        }
        return products;
    }

    // Lấy sản phẩm theo id
    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Product product = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE));
            int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IMAGE_RES_ID));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI));
            product = new Product(name, price, imageResId);
            product.setId(id);
            product.setImageUri(imageUri);
        }
        cursor.close();
        db.close();
        return product;
    }
private static final String DATABASE_NAME = "shopclothes_new_dat.db";
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
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT, "
                + COL_PRICE + " REAL, "
                + COL_IMAGE_RES_ID + " INTEGER, "
                + COL_IMAGE_URI + " TEXT)";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        // Tạo bảng orders với user_id là TEXT
        String CREATE_ORDERS_TABLE = "CREATE TABLE orders ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id TEXT, "
                + "products TEXT, "
                + "total_price REAL, "
                + "created_date TEXT, "
                + "status TEXT)";
        db.execSQL(CREATE_ORDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);

    }

    // Thêm phương thức lưu order
    // Thêm phương thức lưu order với userId là uuid
    public void addOrder(String userId, List<Product> products, double totalPrice, String createdDate, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("user_id", userId);
        values.put("products", serializeProducts(products));
        values.put("total_price", totalPrice);
        values.put("created_date", createdDate);
        values.put("status", status);
        db.insert("orders", null, values);
        db.close();
    }

    private String serializeProducts(List<Product> products) {
        if (products == null || products.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Product p : products) {
            sb.append(p.getId()).append(",");
        }
        return sb.toString();
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
