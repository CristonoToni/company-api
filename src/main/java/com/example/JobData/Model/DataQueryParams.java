package com.example.JobData.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DataQueryParams {
    private String jobTitle;
    private String salaryCmpSign;
    private Double salaryValue;
    private String gender;
    private List<String> fields;
    private List<String> sortFields;
    private String sortType;
    private int limit;
    private int offset;
}