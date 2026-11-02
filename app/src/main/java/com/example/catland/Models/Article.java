package com.example.catland.Models;


import java.util.ArrayList;
import java.util.List;

public class Article {
    private String title;
    private String description;
    private String content;
    private int imageResourceId; // Utilisation de l'ID de ressource drawable
    private String category;
    private List<String> comments;

    public Article() {
        comments = new ArrayList<>();
    }

    // Constructeur avec ID de ressource drawable
    public Article(String title, String description, String content, int imageResourceId, String category) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.imageResourceId = imageResourceId;
        this.category = category;
        this.comments = new ArrayList<>();
    }

    // Getters et Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getImageResourceId() { return imageResourceId; } // Getter pour l'ID de ressource
    public void setImageResourceId(int imageResourceId) { this.imageResourceId = imageResourceId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<String> getComments() { return comments; }
    public void addComment(String comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }
}