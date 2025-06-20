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

public class ProductDashboardAdapter extends RecyclerView.Adapter<ProductDashboardAdapter.ProductViewHolder> {
    private List<Product> products;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onEditProduct(int position, Product product);

        void onDeleteProduct(int position);
    }

    public ProductDashboardAdapter(List<Product> products, OnProductActionListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_dashboard, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName, tvProductPrice;
        private ImageButton btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Product product) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(currencyFormatter.format(product.getPrice()));
            if (product.getImageResId() != 0) {
                ivProductImage.setImageResource(product.getImageResId());
            } else {
                ivProductImage.setImageResource(R.drawable.placeholder_product);
            }
            btnEdit.setOnClickListener(v -> {
                if (listener != null)
                    listener.onEditProduct(getAdapterPosition(), product);
            });
            btnDelete.setOnClickListener(v -> {
                if (listener != null)
                    listener.onDeleteProduct(getAdapterPosition());
            });
        }
    }
}