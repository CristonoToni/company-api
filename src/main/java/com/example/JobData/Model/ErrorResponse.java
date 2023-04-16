package com.example.JobData.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    @JsonProperty("error_status")
    private int status;
    @JsonProperty("error_message")
    private String message;
}
