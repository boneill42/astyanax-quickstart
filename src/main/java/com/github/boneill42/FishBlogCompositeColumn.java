package com.github.boneill42;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.netflix.astyanax.annotations.Component;

public class FishBlogCompositeColumn {
    public static final String BLOG_FIELD = "blog";
    public static final String IMAGE_FIELD = "image";

    @Component(ordinal = 0)
    private long when;

    @Component(ordinal = 1)
    private String fishType;

    @Component(ordinal = 2)
    private String field;

    public FishBlogCompositeColumn() {
    }

    public FishBlogCompositeColumn(long when, String fishType, String field) {
        this.when = when;
        this.fishType = fishType;
        this.field = field;
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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}