package com.example.shopclothes_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    public interface UserClickListener {
        void onUserClicked(User user);

        void onCallClicked(User user);
    }

    private final List<User> users;
    private final UserClickListener listener;

    public UserAdapter(List<User> users, UserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getPhone());
        boolean isBanned = user.isBanned != null && user.isBanned;
        holder.tvStatus.setText(isBanned ? "Banned" : "Active");
        holder.tvStatus.setBackgroundResource(isBanned ? R.drawable.bg_chip_banned : R.drawable.bg_chip_active);
        Glide.with(holder.avatar.getContext())
                .load(user.getAvatarPath())
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.avatar);
        holder.card.setOnClickListener(v -> listener.onUserClicked(user));
        holder.btnCall.setOnClickListener(v -> listener.onCallClicked(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        CircleImageView avatar;
        TextView tvName, tvEmail, tvPhone, tvStatus;
        ImageView btnCall;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_user);
            avatar = itemView.findViewById(R.id.img_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnCall = itemView.findViewById(R.id.btn_call);
        }
    }
}