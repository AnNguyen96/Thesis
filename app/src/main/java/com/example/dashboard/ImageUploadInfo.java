package com.example.dashboard;

public class ImageUploadInfo {

    String title;
    String description;
    String image;
    String search;

    public ImageUploadInfo(){

    }

    public ImageUploadInfo(String title, String description, String image, String search) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.search = search;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getSearch() {
        return search;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}

