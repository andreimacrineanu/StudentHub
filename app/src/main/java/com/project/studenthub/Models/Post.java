package com.project.studenthub.Models;

import android.net.Uri;

import java.io.Serializable;

public class Post implements Serializable {
    private String id;
    private String description;
    private String convertedPicture;
    private String userId;
    private String userDisplayName;
    private String pictureUri;
    private String classId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConvertedPicture() {
        return convertedPicture;
    }

    public void setConvertedPicture(String convertedPicture) {
        this.convertedPicture = convertedPicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Post(){

    }

}
