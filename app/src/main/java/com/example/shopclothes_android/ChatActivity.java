package com.example.shopclothes_android;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final String ADMIN_ID = "admin";

    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewMessages;
    private TextInputEditText etMessage;
    private FloatingActionButton fabSend;
    private LinearLayout layoutEmptyState;
    private CircleImageView imgAdminAvatar;
    private TextView tvAdminName, tvAdminStatus;

    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String chatId;
    private ListenerRegistration messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        initFirebase();
        setupRecyclerView();
        setupClickListeners();
        loadMessages();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        etMessage = findViewById(R.id.etMessage);
        fabSend = findViewById(R.id.fabSend);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        imgAdminAvatar = findViewById(R.id.imgAdminAvatar);
        tvAdminName = findViewById(R.id.tvAdminName);
        tvAdminStatus = findViewById(R.id.tvAdminStatus);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Create a unique chat ID for this user with admin
            chatId = "chat_" + currentUser.getUid() + "_admin";
        } else {
            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        messageList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messageList, currentUser.getUid());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom
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
    }

    private void loadMessages() {
        showLoadingState();

        messageListener = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        hideLoadingState();
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
                        hideLoadingState();

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

        // Clear the input immediately
        etMessage.setText("");

        // Get current user data
        ProfileManager profileManager = ProfileManager.getInstance();
        profileManager.initialize(getApplicationContext());
        User user = profileManager.getCurrentUser();
        String userName = user != null ? user.getName() : "User";

        // Create message object
        Message message = new Message(
                currentUser.getUid(),
                userName,
                ADMIN_ID,
                messageContent,
                false, // Not from admin
                chatId);

        // Save to Firestore
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Message sent successfully");
                    hideEmptyState();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending message", e);
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    // Restore the message if sending failed
                    etMessage.setText(messageContent);
                });

        // Update chat metadata
        updateChatMetadata(userName, messageContent);
    }

    private void updateChatMetadata(String userName, String messageContent) {
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("lastMessage", messageContent);
        chatData.put("lastMessageTime", com.google.firebase.Timestamp.now());
        chatData.put("userId", currentUser.getUid());
        chatData.put("userName", userName);
        
        // Get user data from ProfileManager for additional info
        ProfileManager profileManager = ProfileManager.getInstance();
        User user = profileManager.getCurrentUser();
        if (user != null) {
            chatData.put("userEmail", user.getEmail());
            chatData.put("userAvatarUrl", user.getAvatarPath());
        }
        
        chatData.put("hasUnreadAdminMessage", false);

        db.collection("chats").document(chatId).set(chatData)
                .addOnFailureListener(e -> Log.e(TAG, "Error updating chat metadata", e));
    }

    private void scrollToBottom() {
        if (messageList.size() > 0) {
            recyclerViewMessages.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    private void showLoadingState() {
        // You can add a loading indicator here if needed
    }

    private void hideLoadingState() {
        // Hide loading indicator
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