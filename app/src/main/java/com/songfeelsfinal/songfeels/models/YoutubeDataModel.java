package com.songfeelsfinal.songfeels.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class YoutubeDataModel implements Serializable {
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("publishedAt")
    private String publishedAt;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("video_id")
    private String video_id;

    public YoutubeDataModel() {
    }

    public YoutubeDataModel(String title, String description, String publishedAt, String thumbnail, String video_id) {
        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
        this.thumbnail = thumbnail;
        this.video_id = video_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }
}
