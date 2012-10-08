package com.github.boneill42;

import com.netflix.astyanax.annotations.Component;

public class FishBlog {
    @Component(ordinal = 0)
    public long when;
    @Component(ordinal = 1)
    public String fishtype;
    @Component(ordinal = 2)
    public String field;
        
    public FishBlog() {
    }
}