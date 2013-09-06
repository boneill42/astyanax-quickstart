package com.github.boneill42;

public class FishBlog {
    private String userId;
    private long when;
    private String fishType;
    private String blog;
    private byte[] image;

    public FishBlog() {
    }

    public FishBlog(String userId, long when, String fishType, String blog, byte[] image) {
        this.userId = userId;
        this.when = when;
        this.fishType = fishType;
        this.blog = blog;
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public String getFishType() {
        return fishType;
    }

    public void setFishType(String fishType) {
        this.fishType = fishType;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}