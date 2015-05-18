package com.azsuth.commonmoduleproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by andrewsutherland on 5/18/15.
 */
public class Tests {
    @JsonProperty("results")
    public int results;
    @JsonProperty("data")
    public ArrayList<Test> tests;
}
