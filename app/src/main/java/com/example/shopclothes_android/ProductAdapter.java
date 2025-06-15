package com.example.shopclothes_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final List<String> products;

    public ProductAdapter(List<String> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        String productName = products.get(position);
        holder.tvProductName.setText(productName);

        // Yêu thích
        holder.imgFavorite.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Đã đánh dấu " + productName, Toast.LENGTH_SHORT).show();
        });

        // Thêm vào giỏ hàng
        holder.imgAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(productName);
            Toast.makeText(v.getContext(), "Đã thêm " + productName + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        ImageView imgProduct, imgFavorite, imgAddToCart;

        ProductViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            imgAddToCart = itemView.findViewById(R.id.imgAddToCart);
        }
    }
}
