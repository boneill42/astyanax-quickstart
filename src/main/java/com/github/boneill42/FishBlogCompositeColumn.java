package com.github.boneill42;

import com.netflix.astyanax.annotations.Component;

public class FishBlogCompositeColumn {
    public static final String BLOG_FIELD = "blog";
    public static final String IMAGE_FIELD = "image";

    @Component(ordinal = 0)
    private long when;

    @Component(ordinal = 1)
    private String fishtype;

    @Component(ordinal = 2)
    private String field;

    public FishBlogCompositeColumn() {
    }

    public FishBlogCompositeColumn(long when, String fishtype, String field) {
        this.when = when;
        this.fishtype = fishtype;
        this.field = field;
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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}