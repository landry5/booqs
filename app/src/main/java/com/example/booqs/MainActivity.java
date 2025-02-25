package com.example.booqs;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogin, btnRegister, btnViewBooks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnViewBooks = findViewById(R.id.btnViewBooks);

        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
        btnViewBooks.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BooksActivity.class)));

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, BooksActivity.class));
            finish();
        }
    }
}