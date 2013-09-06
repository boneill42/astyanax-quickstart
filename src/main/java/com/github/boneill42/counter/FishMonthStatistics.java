package com.github.boneill42.counter;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.netflix.astyanax.annotations.Component;

public class FishMonthStatistics {

    private String userId;

    @Component(ordinal = 0)
    private String year;
    
    @Component(ordinal = 1)
    private String month;

    private long fishCatch;

    public FishMonthStatistics() {
    }

    public FishMonthStatistics(String userId) {
        super();
        this.userId = userId;
    }

    public FishMonthStatistics(String userId, String year, String month) {
        this.userId = userId;
        this.year = year;
        this.month = month;
    }
    
    public FishMonthStatistics(String userId, String year, String month, long fishCatch) {
        this.userId = userId;
        this.year = year;
        this.month = month;
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