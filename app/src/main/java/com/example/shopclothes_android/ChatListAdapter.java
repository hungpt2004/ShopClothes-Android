package com.example.shopclothes_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClicked(Chat chat);
    }

    public ChatListAdapter(List<Chat> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_admin, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateChats(List<Chat> newChats) {
        this.chatList = newChats;
        notifyDataSetChanged();
    }

    public void addChat(Chat chat) {
        chatList.add(0, chat); // Add to beginning
        notifyItemInserted(0);
    }

    public void updateChat(Chat updatedChat) {
        for (int i = 0; i < chatList.size(); i++) {
            if (chatList.get(i).getChatId().equals(updatedChat.getChatId())) {
                chatList.set(i, updatedChat);
                notifyItemChanged(i);
                // Move to top if there's a new message
                if (i > 0) {
                    chatList.remove(i);
                    chatList.add(0, updatedChat);
                    notifyItemMoved(i, 0);
                }
                break;
            }
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgUserAvatar;
        private TextView tvUserName;
        private TextView tvUserEmail;
        private TextView tvLastMessage;
        private TextView tvMessageTime;
        private TextView tvUnreadCount;
        private CardView cardUnreadBadge;
        private View viewOnlineIndicator;
        private View viewActiveIndicator;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.img_user_avatar);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvMessageTime = itemView.findViewById(R.id.tv_message_time);
            tvUnreadCount = itemView.findViewById(R.id.tv_unread_count);
            cardUnreadBadge = itemView.findViewById(R.id.card_unread_badge);
            viewOnlineIndicator = itemView.findViewById(R.id.view_online_indicator);
            viewActiveIndicator = itemView.findViewById(R.id.view_active_indicator);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onChatClicked(chatList.get(position));
                    }
                }
            });
        }

        public void bind(Chat chat) {
            // Set user info
            tvUserName.setText(chat.getUserName());
            tvUserEmail.setText(chat.getUserEmail());
            tvLastMessage.setText(chat.getLastMessage());
            tvMessageTime.setText(chat.getFormattedTime());

            // Load user avatar
            if (!chat.getUserAvatarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(chat.getUserAvatarUrl())
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(imgUserAvatar);
            } else {
                imgUserAvatar.setImageResource(R.drawable.default_avatar);
            }

            // Show/hide unread count badge
            if (chat.getUnreadCount() > 0) {
                cardUnreadBadge.setVisibility(View.VISIBLE);
                tvUnreadCount.setText(String.valueOf(chat.getUnreadCount()));
            } else {
                cardUnreadBadge.setVisibility(View.GONE);
            }

            // Show online indicator (always visible for active conversations)
            viewOnlineIndicator.setVisibility(View.VISIBLE);

            // Show active indicator (always visible for conversations)
            viewActiveIndicator.setVisibility(View.VISIBLE);

            // Highlight row if there are unread messages
            if (chat.getUnreadCount() > 0) {
                itemView.setAlpha(1.0f);
                tvUserName.setTextColor(itemView.getContext().getColor(R.color.primary_color));
            } else {
                itemView.setAlpha(0.9f);
                tvUserName.setTextColor(itemView.getContext().getColor(R.color.text_primary));
            }
        }
    }
}