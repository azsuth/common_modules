package com.azsuth.commonmoduleproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by andrewsutherland on 5/18/15.
 */
public class Test {
    @JsonProperty("test1")
    public String test1;
    @JsonProperty("test2")
    public int test2;
}
