package com.example.shopclothes_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<Product> cartItems;
    private final NumberFormat currencyFormatter;
    private final CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged();

        void onItemRemoved(Product product);
    }

    public CartAdapter(List<Product> cartItems, CartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);

        holder.tvCartProductName.setText(product.getName());
        holder.tvCartProductPrice.setText(currencyFormatter.format(product.getPrice()));
        holder.tvQuantity.setText(String.valueOf(product.getQuantity()));
        holder.imgCartProduct.setImageResource(product.getImageResId());

        // Decrease quantity
        holder.btnDecreaseQuantity.setOnClickListener(v -> {
            int currentQty = product.getQuantity();
            if (currentQty > 1) {
                product.setQuantity(currentQty - 1);
                holder.tvQuantity.setText(String.valueOf(currentQty - 1));
                listener.onQuantityChanged();
            }
        });

        // Increase quantity
        holder.btnIncreaseQuantity.setOnClickListener(v -> {
            int currentQty = product.getQuantity();
            product.setQuantity(currentQty + 1);
            holder.tvQuantity.setText(String.valueOf(currentQty + 1));
            listener.onQuantityChanged();
        });

        // Remove item
        holder.btnRemoveItem.setOnClickListener(v -> {
            listener.onItemRemoved(product);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgCartProduct;
        final TextView tvCartProductName;
        final TextView tvCartProductPrice;
        final TextView tvQuantity;
        final ImageButton btnDecreaseQuantity;
        final ImageButton btnIncreaseQuantity;
        final ImageButton btnRemoveItem;

        CartViewHolder(View itemView) {
            super(itemView);
            imgCartProduct = itemView.findViewById(R.id.imgCartProduct);
            tvCartProductName = itemView.findViewById(R.id.tvCartProductName);
            tvCartProductPrice = itemView.findViewById(R.id.tvCartProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
        }
    }
}