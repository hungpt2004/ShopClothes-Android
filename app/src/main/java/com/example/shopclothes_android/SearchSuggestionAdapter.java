package com.example.shopclothes_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.SuggestionViewHolder> {
    private final List<Product> suggestions;
    private final OnSuggestionClickListener listener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

    public interface OnSuggestionClickListener {
        void onSuggestionClick(Product product);
    }

    public SearchSuggestionAdapter(List<Product> suggestions, OnSuggestionClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        Product product = suggestions.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(currencyFormatter.format(product.getPrice()));
        holder.imgProduct.setImageResource(product.getImageResId());
        holder.itemView.setOnClickListener(v -> listener.onSuggestionClick(product));
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice;
        SuggestionViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgSuggestionProduct);
            tvName = itemView.findViewById(R.id.tvSuggestionName);
            tvPrice = itemView.findViewById(R.id.tvSuggestionPrice);
        }
    }
}
