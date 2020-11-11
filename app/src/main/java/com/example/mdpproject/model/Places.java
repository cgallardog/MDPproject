package com.example.mdpproject.model;

public class Places {
    private int imageResource;
    private String name;
    public Places(int image, String place) {
        imageResource = image;
        name = place;
    }

    public int getImageResource() {
        return imageResource;
    }
    public String getName() {
        return name;
    }
    public String toString() {
        return name;
    }
}
