package com.example.booqs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booqs.AddBookActivity;
import com.example.booqs.adapters.BookAdapter;
import com.example.booqs.models.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList, filteredList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FloatingActionButton fabAddBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerViewBooks);
        fabAddBook = findViewById(R.id.fab_add_book);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        bookList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new BookAdapter(filteredList, this);
        recyclerView.setAdapter(adapter);

        loadBooks();

        // Set up the FAB to open the AddBookActivity
        fabAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(BooksActivity.this, com.example.booqs.AddBookActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload books when returning to this activity (e.g., after adding a new book)
        loadBooks();
    }

    private void loadBooks() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        bookList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class);
                            book.setId(document.getId());
                            bookList.add(book);
                        }

                        filteredList.clear();
                        filteredList.addAll(bookList);
                        adapter.notifyDataSetChanged();

                        if (bookList.isEmpty()) {
                            Toast.makeText(BooksActivity.this, "No books available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BooksActivity.this, "Error loading books: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_books, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            // TODO: Implement profile activity
            return true;
        } else if (id == R.id.action_cart) {
            // TODO: Implement cart activity
            return true;
        } else if (id == R.id.action_logout) {
            mAuth.signOut();
            startActivity(new Intent(BooksActivity.this, MainActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_add_book) {
            // Alternative way to launch add book screen from menu
            startActivity(new Intent(BooksActivity.this, AddBookActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterBooks(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(bookList);
        } else {
            String lowercaseQuery = query.toLowerCase();

            for (Book book : bookList) {
                if (book.getTitle().toLowerCase().contains(lowercaseQuery) ||
                        book.getAuthor().toLowerCase().contains(lowercaseQuery) ||
                        book.getCategory().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(book);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBookClick(int position) {
        Book book = filteredList.get(position);
        Intent intent = new Intent(BooksActivity.this, BookDetailActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }
}