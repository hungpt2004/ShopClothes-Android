package com.example.shopclothes_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.google.android.material.button.MaterialButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminChatActivity extends AppCompatActivity {

    private static final String TAG = "AdminChatActivity";
    private static final String ADMIN_ID = "admin";

    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewMessages;
    private TextInputEditText etMessage;
    private FloatingActionButton fabSend;
    private LinearLayout layoutEmptyState;
    private CircleImageView imgUserAvatar;
    private TextView tvUserName, tvUserEmail;
    private ImageButton btnCallUser, btnUserInfo;
    private MaterialButton btnQuickReply;
    private CardView cardQuickReplies;
    private MaterialButton btnQuickReply1, btnQuickReply2, btnQuickReply3;

    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private FirebaseFirestore db;
    private String chatId;
    private String userId;
    private String userName;
    private String userEmail;
    private String userAvatarUrl;
    private ListenerRegistration messageListener;
    private boolean isQuickReplyVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);

        getIntentData();
        initViews();
        initFirebase();
        setupRecyclerView();
        setupClickListeners();
        loadMessages();
        setupUserInfo();
    }

    private void getIntentData() {
        chatId = getIntent().getStringExtra("chatId");
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
        userEmail = getIntent().getStringExtra("userEmail");
        userAvatarUrl = getIntent().getStringExtra("userAvatarUrl");

        if (chatId == null || userId == null) {
            Toast.makeText(this, "Invalid chat data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        etMessage = findViewById(R.id.etMessage);
        fabSend = findViewById(R.id.fabSend);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnCallUser = findViewById(R.id.btnCallUser);
        btnUserInfo = findViewById(R.id.btnUserInfo);
        btnQuickReply = findViewById(R.id.btnQuickReply);
        cardQuickReplies = findViewById(R.id.cardQuickReplies);
        btnQuickReply1 = findViewById(R.id.btnQuickReply1);
        btnQuickReply2 = findViewById(R.id.btnQuickReply2);
        btnQuickReply3 = findViewById(R.id.btnQuickReply3);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        messageList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messageList, ADMIN_ID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void setupClickListeners() {
        toolbar.setNavigationOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        fabSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {
                sendMessage();
                return true;
            }
            return false;
        });

        btnCallUser.setOnClickListener(v -> callUser());
        btnUserInfo.setOnClickListener(v -> showUserInfo());

        btnQuickReply.setOnClickListener(v -> toggleQuickReplies());

        btnQuickReply1.setOnClickListener(v -> useQuickReply("Hello! How can I help you today?"));
        btnQuickReply2.setOnClickListener(v -> useQuickReply("I'm looking into your issue right now..."));
        btnQuickReply3.setOnClickListener(v -> useQuickReply("Thank you for contacting us!"));
    }

    private void setupUserInfo() {
        tvUserName.setText(userName != null ? userName : "User");
        tvUserEmail.setText(userEmail != null ? userEmail : "");

        if (userAvatarUrl != null && !userAvatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(userAvatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(imgUserAvatar);
        }
    }

    private void loadMessages() {
        messageListener = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        messageList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message message = doc.toObject(Message.class);
                            if (message != null) {
                                message.setId(doc.getId());
                                messageList.add(message);
                            }
                        }

                        messageAdapter.updateMessages(messageList);
                        scrollToBottom();

                        if (messageList.isEmpty()) {
                            showEmptyState();
                        } else {
                            hideEmptyState();
                        }
                    }
                });
    }

    private void sendMessage() {
        String messageContent = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(messageContent)) {
            return;
        }

        // Clear input immediately
        etMessage.setText("");
        hideQuickReplies();

        // Create admin message
        Message message = new Message(
                ADMIN_ID,
                "Admin Support",
                userId,
                messageContent,
                true, // From admin
                chatId);

        // Save to Firestore
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Admin message sent successfully");
                    hideEmptyState();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending admin message", e);
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    etMessage.setText(messageContent);
                });

        // Update chat metadata
        updateChatMetadata(messageContent);
    }

    private void updateChatMetadata(String messageContent) {
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("lastMessage", messageContent);
        chatData.put("lastMessageTime", com.google.firebase.Timestamp.now());
        chatData.put("userId", userId);
        chatData.put("userName", userName);
        chatData.put("userEmail", userEmail);
        chatData.put("userAvatarUrl", userAvatarUrl);
        chatData.put("hasUnreadAdminMessage", true);

        db.collection("chats").document(chatId).set(chatData)
                .addOnFailureListener(e -> Log.e(TAG, "Error updating chat metadata", e));
    }

    private void callUser() {
        // Get user phone from Firestore
        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String phone = document.getString("phone");
                        if (phone != null && !phone.isEmpty()) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phone));
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "No phone number available", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to get user info", Toast.LENGTH_SHORT).show();
                });
    }

    private void showUserInfo() {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("user_name", userName);
        intent.putExtra("user_email", userEmail);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    private void toggleQuickReplies() {
        if (isQuickReplyVisible) {
            hideQuickReplies();
        } else {
            showQuickReplies();
        }
    }

    private void showQuickReplies() {
        cardQuickReplies.setVisibility(View.VISIBLE);
        isQuickReplyVisible = true;
    }

    private void hideQuickReplies() {
        cardQuickReplies.setVisibility(View.GONE);
        isQuickReplyVisible = false;
    }

    private void useQuickReply(String message) {
        etMessage.setText(message);
        hideQuickReplies();
        etMessage.setSelection(message.length()); // Move cursor to end
    }

    private void scrollToBottom() {
        if (messageList.size() > 0) {
            recyclerViewMessages.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    private void showEmptyState() {
        layoutEmptyState.setVisibility(View.VISIBLE);
        recyclerViewMessages.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        layoutEmptyState.setVisibility(View.GONE);
        recyclerViewMessages.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            messageListener.remove();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}