package com.example.catland.Models;


public class Cat {
    private String name;
    private String origin;
    private String description;
    private int imageResId;

    public Cat(String name, String origin, String description, int imageResId) {
        this.name = name;
        this.origin = origin;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }
}
