package com.github.boneill42;

public class FishBlog {
    private String userid;
    private long when;
    private String fishtype;
    private String blog;
    private byte[] image;

    public FishBlog() {
    }

    public FishBlog(String userid, long when, String fishtype, String blog, byte[] image) {
        this.userid = userid;
        this.when = when;
        this.fishtype = fishtype;
        this.blog = blog;
        this.image = image;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public String getFishtype() {
        return fishtype;
    }

    public void setFishtype(String fishtype) {
        this.fishtype = fishtype;
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