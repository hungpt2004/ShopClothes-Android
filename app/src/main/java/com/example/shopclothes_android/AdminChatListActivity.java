package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminChatListActivity extends AppCompatActivity implements ChatListAdapter.OnChatClickListener {

    private static final String TAG = "AdminChatListActivity";
    
    private RecyclerView rvChats;
    private ChatListAdapter chatAdapter;
    private List<Chat> chatList = new ArrayList<>();
    private List<Chat> allChats = new ArrayList<>();
    private FirebaseFirestore db;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNav;
    private EditText etSearch;
    private MaterialButton btnFilter;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;

    private ListenerRegistration chatsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat_list);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupBottomNavigation();
        
        db = FirebaseFirestore.getInstance();
        loadChats();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        rvChats = findViewById(R.id.rv_chats);
        etSearch = findViewById(R.id.et_search);
        btnFilter = findViewById(R.id.btn_filter);
        progressBar = findViewById(R.id.progress_bar);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatListAdapter(chatList, this);
        rvChats.setLayoutManager(new LinearLayoutManager(this));
        rvChats.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterChats(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_chat); // We'll add this to the menu
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_admin) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_product_dashboard) {
                startActivity(new Intent(this, ProductDashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_chat) {
                return true; // Already in chat list
            }
            
            return false;
        });
    }

    private void loadChats() {
        showLoadingState();
        
        chatsListener = db.collection("chats")
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    hideLoadingState();
                    
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        Toast.makeText(this, "Failed to load conversations", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        allChats.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Chat chat = doc.toObject(Chat.class);
                            if (chat != null) {
                                chat.setChatId(doc.getId());
                                allChats.add(chat);
                            }
                        }
                        
                        chatList.clear();
                        chatList.addAll(allChats);
                        chatAdapter.updateChats(chatList);
                        
                        if (chatList.isEmpty()) {
                            showEmptyState();
                        } else {
                            hideEmptyState();
                        }
                    }
                });
    }

    private void filterChats(String query) {
        chatList.clear();
        if (query.isEmpty()) {
            chatList.addAll(allChats);
        } else {
            for (Chat chat : allChats) {
                if (chat.getUserName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)) ||
                    chat.getUserEmail().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)) ||
                    chat.getLastMessage().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                    chatList.add(chat);
                }
            }
        }
        chatAdapter.updateChats(chatList);
    }

    private void showFilterDialog() {
        // TODO: Implement filter dialog (e.g., by unread, recent, etc.)
        Toast.makeText(this, "Filter feature coming soon!", Toast.LENGTH_SHORT).show();
    }



    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
        rvChats.setAlpha(0.5f);
    }

    private void hideLoadingState() {
        progressBar.setVisibility(View.GONE);
        rvChats.setAlpha(1.0f);
    }

    private void showEmptyState() {
        layoutEmptyState.setVisibility(View.VISIBLE);
        rvChats.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        layoutEmptyState.setVisibility(View.GONE);
        rvChats.setVisibility(View.VISIBLE);
    }

    @Override
    public void onChatClicked(Chat chat) {
        Intent intent = new Intent(this, AdminChatActivity.class);
        intent.putExtra("chatId", chat.getChatId());
        intent.putExtra("userId", chat.getUserId());
        intent.putExtra("userName", chat.getUserName());
        intent.putExtra("userEmail", chat.getUserEmail());
        intent.putExtra("userAvatarUrl", chat.getUserAvatarUrl());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatsListener != null) {
            chatsListener.remove();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_chat);
    }
} 