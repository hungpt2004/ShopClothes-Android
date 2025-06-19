package com.example.shopclothes_android;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.radiobutton.MaterialRadioButton;
import android.widget.RadioGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputLayout tilName, tilPhone, tilBirthDate;
    private TextInputEditText etName, etPhone, etBirthDate;
    private RadioGroup rgGender;
    private MaterialButton btnSave;
    private FirebaseFirestore db;
    private String uid;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilName = findViewById(R.id.til_name);
        tilPhone = findViewById(R.id.til_phone);
        tilBirthDate = findViewById(R.id.til_birth_date);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etBirthDate = findViewById(R.id.et_birth_date);
        rgGender = findViewById(R.id.rg_gender);
        btnSave = findViewById(R.id.btn_save);
    }

    private void loadUserData() {
        User user = ProfileManager.getInstance().getCurrentUser();
        if (user != null) {
            etName.setText(user.getName());
            etPhone.setText(user.getPhone());
            etBirthDate.setText(user.getBirthDate());
            String gender = user.getGender();
            if (gender.equalsIgnoreCase("Male"))
                rgGender.check(R.id.rb_male);
            else if (gender.equalsIgnoreCase("Female"))
                rgGender.check(R.id.rb_female);
            else
                rgGender.check(R.id.rb_other);
        }
    }

    private void setupListeners() {
        etBirthDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveProfile());
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            etBirthDate.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();
        int genderId = rgGender.getCheckedRadioButtonId();
        String gender = "Other";
        if (genderId == R.id.rb_male)
            gender = "Male";
        else if (genderId == R.id.rb_female)
            gender = "Female";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(birthDate)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setText("Saving...");
        btnSave.setEnabled(false);

        User user = ProfileManager.getInstance().getCurrentUser();
        user.setName(name);
        user.setPhone(phone);
        user.setBirthDate(birthDate);
        user.setGender(gender);
        ProfileManager.getInstance().saveUser(user);

        if (uid != null) {
            db.collection("users").document(uid)
                    .update("name", name, "phone", phone, "birthDate", birthDate, "gender", gender)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                        btnSave.setText("Save");
                        btnSave.setEnabled(true);
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update profile in database", Toast.LENGTH_SHORT).show();
                        btnSave.setText("Save");
                        btnSave.setEnabled(true);
                    });
        } else {
            btnSave.setText("Save");
            btnSave.setEnabled(true);
            setResult(RESULT_OK);
            finish();
        }
    }
}