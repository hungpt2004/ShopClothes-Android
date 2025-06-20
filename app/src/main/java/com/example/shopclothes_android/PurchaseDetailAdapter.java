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

public class PurchaseDetailAdapter extends RecyclerView.Adapter<PurchaseDetailAdapter.PurchaseItemViewHolder> {
    private List<Product> items;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

    public PurchaseDetailAdapter(List<Product> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PurchaseItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_detail, parent, false);
        return new PurchaseItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseItemViewHolder holder, int position) {
        Product item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class PurchaseItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvQuantity;
        private TextView tvTotalPrice;

        public PurchaseItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
        }

        public void bind(Product product) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(currencyFormatter.format(product.getPrice()));
            tvQuantity.setText("Qty: " + product.getQuantity());
            tvTotalPrice.setText(currencyFormatter.format(product.getTotalPrice()));

            // Set product image
            if (product.getImageResId() != 0) {
                ivProductImage.setImageResource(product.getImageResId());
            } else {
                ivProductImage.setImageResource(R.drawable.placeholder_product);
            }
        }
    }
}