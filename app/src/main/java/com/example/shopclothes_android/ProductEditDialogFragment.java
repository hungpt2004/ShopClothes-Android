package com.example.shopclothes_android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import java.io.InputStream;

public class ProductEditDialogFragment extends DialogFragment {
    public interface ProductEditListener {
        void onProductSaved(Product product, int editIndex);
    }

    private static final int PICK_IMAGE_REQUEST = 101;
    private TextInputLayout tilName, tilPrice;
    private EditText etName, etPrice;
    private ImageView ivProductImage;
    private MaterialButton btnSave, btnCancel, btnPickImage;
    private Product productToEdit;
    private int editIndex = -1;
    private int imageResId = 0;
    private ProductEditListener listener;

    public static ProductEditDialogFragment newInstance(@Nullable Product product, int editIndex) {
        ProductEditDialogFragment fragment = new ProductEditDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("product", product);
        args.putInt("edit_index", editIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProductEditListener) {
            listener = (ProductEditListener) context;
        } else if (getParentFragment() instanceof ProductEditListener) {
            listener = (ProductEditListener) getParentFragment();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_product_edit, container, false);
        tilName = view.findViewById(R.id.til_name);
        tilPrice = view.findViewById(R.id.til_price);
        etName = view.findViewById(R.id.et_name);
        etPrice = view.findViewById(R.id.et_price);
        ivProductImage = view.findViewById(R.id.iv_product_image);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnPickImage = view.findViewById(R.id.btn_pick_image);

        if (getArguments() != null) {
            productToEdit = (Product) getArguments().getSerializable("product");
            editIndex = getArguments().getInt("edit_index", -1);
            if (productToEdit != null) {
                etName.setText(productToEdit.getName());
                etPrice.setText(String.valueOf(productToEdit.getPrice()));
                imageResId = productToEdit.getImageResId();
                if (imageResId != 0) {
                    ivProductImage.setImageResource(imageResId);
                }
            }
        }

        btnPickImage.setOnClickListener(v -> pickImage());
        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveProduct());

        return view;
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null
                && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ivProductImage.setImageBitmap(bitmap);
                imageResId = 0; // Custom image, not a resource
            } catch (Exception e) {
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        tilName.setError(null);
        tilPrice.setError(null);
        boolean valid = true;
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Name required");
            valid = false;
        }
        double price = 0;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0)
                throw new NumberFormatException();
        } catch (Exception e) {
            tilPrice.setError("Valid price required");
            valid = false;
        }
        if (!valid)
            return;
        Product product = new Product(name, price, imageResId);
        if (listener != null) {
            listener.onProductSaved(product, editIndex);
        }
        dismiss();
    }
}