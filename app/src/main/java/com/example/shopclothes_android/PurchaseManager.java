package com.example.shopclothes_android;

import android.content.Context;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PurchaseManager {
    private static final String TAG = "PurchaseManager";
    private static final String PURCHASE_FILE = "purchases.dat";
    private static PurchaseManager instance;
    private List<Purchase> purchases;
    private Context context;

    private PurchaseManager() {
        purchases = new ArrayList<>();
    }

    public static synchronized PurchaseManager getInstance() {
        if (instance == null) {
            instance = new PurchaseManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        this.context = context;
        loadPurchases();
    }

    public void savePurchase(List<Product> items, double subtotal, double shippingFee, double total, String userEmail) {
        String purchaseId = generatePurchaseId();
        Purchase purchase = new Purchase(purchaseId, new ArrayList<>(items), subtotal, shippingFee, total, userEmail);
        purchases.add(purchase);
        savePurchasesToStorage();
        Log.d(TAG, "Purchase saved: " + purchaseId);
    }

    public List<Purchase> getPurchases() {
        return new ArrayList<>(purchases);
    }

    public List<Purchase> getPurchasesByUser(String userEmail) {
        List<Purchase> userPurchases = new ArrayList<>();
        for (Purchase purchase : purchases) {
            if (purchase.getUserEmail().equals(userEmail)) {
                userPurchases.add(purchase);
            }
        }
        // Sort by date (newest first)
        Collections.sort(userPurchases, (p1, p2) -> p2.getPurchaseDate().compareTo(p1.getPurchaseDate()));
        return userPurchases;
    }

    public List<Purchase> getPurchasesByUserPaginated(String userEmail, int page, int pageSize) {
        List<Purchase> allUserPurchases = getPurchasesByUser(userEmail);
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allUserPurchases.size());

        if (startIndex >= allUserPurchases.size()) {
            return new ArrayList<>();
        }

        return allUserPurchases.subList(startIndex, endIndex);
    }

    public int getTotalPurchaseCount(String userEmail) {
        List<Purchase> userPurchases = getPurchasesByUser(userEmail);
        return userPurchases.size();
    }

    public boolean hasMorePurchases(String userEmail, int currentPage, int pageSize) {
        int totalCount = getTotalPurchaseCount(userEmail);
        return (currentPage + 1) * pageSize < totalCount;
    }

    private String generatePurchaseId() {
        return "PURCHASE_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void loadPurchases() {
        try {
            FileInputStream fis = context.openFileInput(PURCHASE_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            purchases = (List<Purchase>) ois.readObject();
            ois.close();
            fis.close();
            Log.d(TAG, "Purchases loaded: " + purchases.size() + " purchases");
        } catch (Exception e) {
            Log.e(TAG, "Error loading purchases", e);
            purchases = new ArrayList<>();
        }
    }

    private void savePurchasesToStorage() {
        try {
            FileOutputStream fos = context.openFileOutput(PURCHASE_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(purchases);
            oos.close();
            fos.close();
            Log.d(TAG, "Purchases saved to storage");
        } catch (Exception e) {
            Log.e(TAG, "Error saving purchases", e);
        }
    }
}