package com.example.booqs;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booqs.R;
import com.example.booqs.models.Book;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddBookActivity extends AppCompatActivity {
    private EditText editTitle, editAuthor, editDescription;
    private ImageView imageBookCover;
    private Button btnUploadCover, btnSaveBook;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Initialize Firestore and Storage
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("book_covers");

        // Initialize views
        editTitle = findViewById(R.id.edit_book_title);
        editAuthor = findViewById(R.id.edit_book_author);
        editDescription = findViewById(R.id.edit_book_description);
        imageBookCover = findViewById(R.id.image_book_cover);
        btnUploadCover = findViewById(R.id.btn_upload_cover);
        btnSaveBook = findViewById(R.id.btn_save_book);
        progressBar = findViewById(R.id.progress_bar);

        // Set up click listeners
        btnUploadCover.setOnClickListener(v -> openFileChooser());
        btnSaveBook.setOnClickListener(v -> saveBook());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageBookCover.setImageURI(imageUri);
        }
    }

    private void saveBook() {
        String title = editTitle.getText().toString().trim();
        String author = editAuthor.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty()) {
            editTitle.setError("Title is required");
            editTitle.requestFocus();
            return;
        }

        if (author.isEmpty()) {
            editAuthor.setError("Author is required");
            editAuthor.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            // Upload image first, then save book with image URL
            uploadImageAndSaveBook(title, author, description);
        } else {
            // Save book without image
            saveBookToFirestore(title, author, description, "");
        }
    }

    private void uploadImageAndSaveBook(String title, String author, String description) {
        final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." +
                getFileExtension(imageUri));

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveBookToFirestore(title, author, description, imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddBookActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void saveBookToFirestore(String title, String author, String description, String imageUrl) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setImageUrl(imageUrl);

        db.collection("books")
                .add(book)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddBookActivity.this, "Book added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddBookActivity.this, "Error adding book: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
