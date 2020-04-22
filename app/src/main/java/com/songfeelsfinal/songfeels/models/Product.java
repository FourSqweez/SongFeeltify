package com.songfeelsfinal.songfeels.models;

public class Product {
    private String Title;
    private String Description;
    private int Cover;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getCover() {
        return Cover;
    }

    public void setCover(int cover) {
        Cover = cover;
    }
}
