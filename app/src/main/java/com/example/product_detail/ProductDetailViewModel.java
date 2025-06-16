package com.example.product_detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProductDetailViewModel extends ViewModel {

    // State data using LiveData
    private MutableLiveData<ProductState> productState = new MutableLiveData<>();
    private MutableLiveData<Boolean> isBookmarked = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isDescriptionExpanded = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // Getters for LiveData
    public LiveData<ProductState> getProductState() {
        return productState;
    }

    public LiveData<Boolean> getIsBookmarked() {
        return isBookmarked;
    }

    public LiveData<Boolean> getIsDescriptionExpanded() {
        return isDescriptionExpanded;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Methods to update state
    public void loadProductData(String title, String description, float rating, int reviewCount, String price, String totalPrice) {
        isLoading.setValue(true);

        ProductState state = new ProductState();
        state.title = title != null ? title : "Medium Title";
        state.description = description != null ? description : "Description text about something on this page...";
        state.rating = rating;
        state.reviewCount = reviewCount;
        state.price = price != null ? price : "$25/day";
        state.totalPrice = totalPrice != null ? totalPrice : "$700 total";

        productState.setValue(state);
        isLoading.setValue(false);
    }

    public void toggleBookmark() {
        Boolean currentState = isBookmarked.getValue();
        isBookmarked.setValue(currentState == null ? true : !currentState);
    }

    public void toggleDescription() {
        Boolean currentState = isDescriptionExpanded.getValue();
        isDescriptionExpanded.setValue(currentState == null ? true : !currentState);
    }

    // Inner class để định nghĩa Product State
    public static class ProductState {
        public String title;
        public String description;
        public float rating;
        public int reviewCount;
        public String price;
        public String totalPrice;
    }
}