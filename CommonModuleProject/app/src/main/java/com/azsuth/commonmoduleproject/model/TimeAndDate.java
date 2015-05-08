package com.azsuth.commonmoduleproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by andrewsutherland on 5/8/15.
 */
public class TimeAndDate {
    @JsonProperty("time")
    public String time;
    @JsonProperty("milliseconds_since_epoch")
    public long epoch;
    @JsonProperty("date")
    public String date;
}
