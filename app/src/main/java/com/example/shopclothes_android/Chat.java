package com.example.shopclothes_android;

import com.google.firebase.Timestamp;

public class Chat {
    private String chatId;
    private String userId;
    private String userName;
    private String userEmail;
    private String userAvatarUrl;
    private String lastMessage;
    private Timestamp lastMessageTime;
    private int unreadCount;
    private boolean hasUnreadAdminMessage;

    public Chat() {
        // Default constructor required for Firestore
    }

    public Chat(String chatId, String userId, String userName, String userEmail) {
        this.chatId = chatId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.unreadCount = 0;
        this.hasUnreadAdminMessage = false;
    }

    // Getters
    public String getChatId() {
        return chatId != null ? chatId : "";
    }

    public String getUserId() {
        return userId != null ? userId : "";
    }

    public String getUserName() {
        return userName != null ? userName : "User";
    }

    public String getUserEmail() {
        return userEmail != null ? userEmail : "";
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl != null ? userAvatarUrl : "";
    }

    public String getLastMessage() {
        return lastMessage != null ? lastMessage : "";
    }

    public Timestamp getLastMessageTime() {
        return lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public boolean hasUnreadAdminMessage() {
        return hasUnreadAdminMessage;
    }

    // Setters
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLastMessageTime(Timestamp lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setHasUnreadAdminMessage(boolean hasUnreadAdminMessage) {
        this.hasUnreadAdminMessage = hasUnreadAdminMessage;
    }

    // Utility methods
    public String getFormattedTime() {
        if (lastMessageTime != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            return sdf.format(lastMessageTime.toDate());
        }
        return "";
    }

    public String getFormattedDate() {
        if (lastMessageTime != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy",
                    java.util.Locale.getDefault());
            return sdf.format(lastMessageTime.toDate());
        }
        return "";
    }
}