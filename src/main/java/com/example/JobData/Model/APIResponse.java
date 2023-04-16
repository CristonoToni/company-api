package com.example.JobData.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class APIResponse {
    @JsonProperty("status")
    private int status;
    @JsonProperty("has_more")
    private boolean hasMoreData;
    @JsonProperty("data_count")
    private int count;
    @JsonProperty("data")
    private List<JobDataModel> data;
}


