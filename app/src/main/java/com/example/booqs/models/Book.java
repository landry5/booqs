package com.example.booqs.models;

import java.io.Serializable;

public class Book implements Serializable {
    private String id;
    private String title;
    private String author;
    private String description;
    private String coverUrl;
    private double price;
    private String category;
    private long publishDate;
    private int pageCount;
    private float rating;
    private boolean isFeatured;

    // Empty constructor needed for Firestore
    public Book() {}

    public Book(String id, String title, String author, String description, String coverUrl,
                double price, String category, long publishDate, int pageCount, float rating,
                boolean isFeatured) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.coverUrl = coverUrl;
        this.price = price;
        this.category = category;
        this.publishDate = publishDate;
        this.pageCount = pageCount;
        this.rating = rating;
        this.isFeatured = isFeatured;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getPublishDate() { return publishDate; }
    public void setPublishDate(long publishDate) { this.publishDate = publishDate; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public void setImageUrl(String imageUrl) {
        
    }
}