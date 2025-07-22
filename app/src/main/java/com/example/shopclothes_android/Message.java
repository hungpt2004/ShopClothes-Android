package com.example.shopclothes_android;

import com.google.firebase.Timestamp;

public class Message {
    private String id;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String content;
    private Timestamp timestamp;
    private boolean isFromAdmin;
    private String chatId;

    public Message() {
        // Default constructor required for Firestore
    }

    public Message(String senderId, String senderName, String receiverId, String content, boolean isFromAdmin,
            String chatId) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.content = content;
        this.isFromAdmin = isFromAdmin;
        this.chatId = chatId;
        this.timestamp = Timestamp.now();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId != null ? senderId : "";
    }

    public String getSenderName() {
        return senderName != null ? senderName : "";
    }

    public String getReceiverId() {
        return receiverId != null ? receiverId : "";
    }

    public String getContent() {
        return content != null ? content : "";
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public boolean isFromAdmin() {
        return isFromAdmin;
    }

    public String getChatId() {
        return chatId != null ? chatId : "";
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setFromAdmin(boolean fromAdmin) {
        isFromAdmin = fromAdmin;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    // Utility methods
    public String getFormattedTime() {
        if (timestamp != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            return sdf.format(timestamp.toDate());
        }
        return "";
    }

    public String getFormattedDate() {
        if (timestamp != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy",
                    java.util.Locale.getDefault());
            return sdf.format(timestamp.toDate());
        }
        return "";
    }
}