package com.example.shopclothes_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private final List<BannerItem> bannerList;
    private final Context context;

    public BannerAdapter(Context context, List<BannerItem> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_slide, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerItem item = bannerList.get(position);
        Glide.with(context)
                .load(item.getImageUrl())
                .centerCrop()
                .into(holder.imgBanner);
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        TextView tvTitle, tvSubtitle;
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            tvTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvSubtitle = itemView.findViewById(R.id.tvBannerSubtitle);
        }
    }

    public static class BannerItem {
        private final String imageUrl;
        private final String title;
        private final String subtitle;
        public BannerItem(String imageUrl, String title, String subtitle) {
            this.imageUrl = imageUrl;
            this.title = title;
            this.subtitle = subtitle;
        }
        public String getImageUrl() { return imageUrl; }
        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
    }
}
