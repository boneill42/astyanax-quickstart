package com.github.boneill42.counter;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.netflix.astyanax.annotations.Component;

public class FishStatistics {

    private String userId;

    @Component(ordinal = 0)
    private String year;

    private long fishCatch;

    public FishStatistics() {
    }

    public FishStatistics(String userId) {
        this.userId = userId;
    }

    public FishStatistics(String userId, String year) {
        this.userId = userId;
        this.year = year;
    }
    
    public FishStatistics(String userId, String year, long fishCatch) {
        this.userId = userId;
        this.year = year;
        this.fishCatch = fishCatch;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public long getFishCatch() {
        return fishCatch;
    }

    public void setFishCatch(long fishCatch) {
        this.fishCatch = fishCatch;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}