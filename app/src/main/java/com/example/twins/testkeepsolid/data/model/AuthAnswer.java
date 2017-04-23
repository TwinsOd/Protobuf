package com.example.twins.testkeepsolid.data.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthAnswer {
    @JsonProperty("response")
    private int response;

    @JsonProperty("session")
    private String session;

    public int getResponse() {
        return response;
    }

    public String getSession() {
        return session;
    }
}
