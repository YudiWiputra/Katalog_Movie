package com.example.katalogmovie.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.List;

public class KeywordRespone {
    @SerializedName("results")
    private List<KeywordResult> results;

    public List<KeywordResult> getResults() {
        return results;
    }
}
