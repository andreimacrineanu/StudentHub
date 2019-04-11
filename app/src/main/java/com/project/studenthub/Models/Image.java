package com.project.studenthub.Models;

import android.net.Uri;

public class Image {
    private String image_title;
    private Uri uri;
    private String path;
    public Image(){

    }

    public String getImage_title() {
        return image_title;
    }

    public Uri getUri() {
        return uri;
    }

    public void setImage_title(String image_title) {
        this.image_title = image_title;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
