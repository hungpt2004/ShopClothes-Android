package com.example.shopclothes_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final List<Product> products;
    private final NumberFormat currencyFormatter;
    private final ProductClickListener listener;

    public interface ProductClickListener {
        void onFavoriteClicked(Product product);

        void onAddToCartClicked(Product product);

        void onProductClicked(Product product);
    }

    public ProductAdapter(List<Product> products, ProductClickListener listener) {
        this.products = products;
        this.listener = listener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
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
        try {
            Product product = products.get(position);
            holder.tvProductName.setText(product.getName());
            holder.imgProduct.setImageResource(product.getImageResId());
            holder.tvPrice.setText(currencyFormatter.format(product.getPrice()));

            // Update favorite icon state
            updateFavoriteIcon(holder.imgFavorite, product.isFavorite());

            // Favorite button click
            holder.imgFavorite.setOnClickListener(v -> {
                try {
                    listener.onFavoriteClicked(product);
                    updateFavoriteIcon(holder.imgFavorite, product.isFavorite());
                    Toast.makeText(v.getContext(),
                            product.isFavorite() ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "Error updating favorite", Toast.LENGTH_SHORT).show();
                }
            });

            // Add to cart button click
            holder.imgAddToCart.setOnClickListener(v -> {
                try {
                    listener.onAddToCartClicked(product);
                    Toast.makeText(v.getContext(),
                            "Đã thêm " + product.getName() + " vào giỏ hàng!",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "Error adding to cart", Toast.LENGTH_SHORT).show();
                }
            });

            // Item click mở ProductDetailActivity
            holder.itemView.setOnClickListener(v -> {
                try {
                    listener.onProductClicked(product);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "Error opening product detail", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            // Log error và không làm gì để tránh crash
        }
    }

    private void updateFavoriteIcon(ImageView imageView, boolean isFavorite) {
        try {
            imageView.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
            int color = isFavorite ? 
                ContextCompat.getColor(imageView.getContext(), R.color.primary_color) :
                ContextCompat.getColor(imageView.getContext(), R.color.text_hint);
            imageView.setColorFilter(color);
        } catch (Exception e) {
            // Fallback: chỉ đổi icon, không đổi màu
            imageView.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvPrice;
        ImageView imgProduct, imgFavorite, imgAddToCart;

        ProductViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            imgAddToCart = itemView.findViewById(R.id.imgAddToCart);
        }
    }
}
