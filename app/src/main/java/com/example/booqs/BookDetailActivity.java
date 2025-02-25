package com.example.booqs;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.booqs.models.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView ivCover;
    private TextView tvTitle, tvAuthor, tvDescription, tvPrice, tvCategory, tvPublishDate, tvPageCount;
    private RatingBar ratingBar;
    private Button btnAddToCart, btnBack;

    private Book book;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        ivCover = findViewById(R.id.ivDetailCover);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvAuthor = findViewById(R.id.tvDetailAuthor);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvPublishDate = findViewById(R.id.tvDetailPublishDate);
        tvPageCount = findViewById(R.id.tvDetailPageCount);
        ratingBar = findViewById(R.id.ratingBar);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBack = findViewById(R.id.btnBack);

        // Get book from intent
        book = (Book) getIntent().getSerializableExtra("book");

        if (book != null) {
            displayBookDetails();
        } else {
            Toast.makeText(this, "Error: Book details not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void displayBookDetails() {
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        tvDescription.setText(book.getDescription());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        tvPrice.setText(currencyFormat.format(book.getPrice()));

        tvCategory.setText(book.getCategory());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        tvPublishDate.setText(dateFormat.format(new Date(book.getPublishDate())));

        tvPageCount.setText(book.getPageCount() + " pages");

        ratingBar.setRating(book.getRating());

        // Load book cover
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(this)
                    .load(book.getCoverUrl())
                    .placeholder(R.drawable.placeholder_book)
                    .error(R.drawable.placeholder_book)
                    .into(ivCover);
        } else {
            ivCover.setImageResource(R.drawable.placeholder_book);
        }
    }

    private void addToCart() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to add items to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("bookId", book.getId());
        cartItem.put("title", book.getTitle());
        cartItem.put("author", book.getAuthor());
        cartItem.put("coverUrl", book.getCoverUrl());
        cartItem.put("price", book.getPrice());
        cartItem.put("quantity", 1);
        cartItem.put("addedAt", FieldValue.serverTimestamp());

        db.collection("users").document(userId)
                .collection("cart")
                .whereEqualTo("bookId", book.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Add new item to cart
                            db.collection("users").document(userId)
                                    .collection("cart")
                                    .add(cartItem)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(BookDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(BookDetailActivity.this, "Failed to add to cart: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Update quantity for existing item
                            DocumentReference docRef = task.getResult().getDocuments().get(0).getReference();
                            docRef.update("quantity", FieldValue.increment(1))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(BookDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(BookDetailActivity.this, "Failed to update cart: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(BookDetailActivity.this, "Error checking cart: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}