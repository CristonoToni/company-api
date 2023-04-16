package com.example.JobData.Service;

import com.example.JobData.Model.DataQueryParams;
import org.springframework.http.converter.json.MappingJacksonValue;

public interface IJobDataService {
    MappingJacksonValue getResult(DataQueryParams params);
}
