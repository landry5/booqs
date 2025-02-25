package com.example.booqs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.booqs.R;
import com.example.booqs.models.Book;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> bookList;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(int position);
    }

    public BookAdapter(List<Book> bookList, OnBookClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        holder.tvPrice.setText(format.format(book.getPrice()));

        // Load book cover image using Glide
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(book.getCoverUrl())
                    .placeholder(R.drawable.placeholder_book)
                    .error(R.drawable.placeholder_book)
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.placeholder_book);
        }

        // Show featured badge if book is featured
        if (book.isFeatured()) {
            holder.tvFeatured.setVisibility(View.VISIBLE);
        } else {
            holder.tvFeatured.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvPrice, tvFeatured;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCover = itemView.findViewById(R.id.ivBookCover);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvPrice = itemView.findViewById(R.id.tvBookPrice);
            tvFeatured = itemView.findViewById(R.id.tvFeatured);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onBookClick(position);
                }
            }
        }
    }
}