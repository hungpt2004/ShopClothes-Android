package com.example.shopclothes_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Purchase> purchases;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    private static final int VIEW_TYPE_PURCHASE = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private boolean isLoading = false;
    private OnPurchaseClickListener onPurchaseClickListener;
    private OnLoadMoreListener onLoadMoreListener;

    public interface OnPurchaseClickListener {
        void onPurchaseClick(Purchase purchase);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public PurchaseHistoryAdapter(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    public void setOnPurchaseClickListener(OnPurchaseClickListener listener) {
        this.onPurchaseClickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
        if (loading) {
            notifyItemInserted(purchases.size());
        } else {
            notifyItemRemoved(purchases.size());
        }
    }

    public void addPurchases(List<Purchase> newPurchases) {
        int startPosition = purchases.size();
        purchases.addAll(newPurchases);
        notifyItemRangeInserted(startPosition, newPurchases.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == purchases.size() && isLoading) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_PURCHASE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_history, parent, false);
            return new PurchaseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PurchaseViewHolder) {
            Purchase purchase = purchases.get(position);
            ((PurchaseViewHolder) holder).bind(purchase);
        }
    }

    @Override
    public int getItemCount() {
        return purchases.size() + (isLoading ? 1 : 0);
    }

    class PurchaseViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPurchaseId;
        private TextView tvPurchaseDate;
        private TextView tvItemCount;
        private TextView tvTotal;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPurchaseId = itemView.findViewById(R.id.tv_purchase_id);
            tvPurchaseDate = itemView.findViewById(R.id.tv_purchase_date);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            tvTotal = itemView.findViewById(R.id.tv_total);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onPurchaseClickListener != null) {
                    onPurchaseClickListener.onPurchaseClick(purchases.get(position));
                }
            });
        }

        public void bind(Purchase purchase) {
            tvPurchaseId.setText("Order #" + purchase.getPurchaseId());
            tvPurchaseDate.setText(dateFormatter.format(purchase.getPurchaseDate()));
            tvItemCount.setText(purchase.getItems().size() + " item(s)");
            tvTotal.setText(currencyFormatter.format(purchase.getTotal()));
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}