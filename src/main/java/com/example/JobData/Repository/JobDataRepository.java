package com.example.JobData.Repository;

import com.example.JobData.Model.JobDataModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class JobDataRepository implements IJobDataRepository {
    private final Logger logger = Logger.getLogger(getClass().getName());

    private final String regexSalary = "(\\d+(?:\\.\\d+)?)([kK])?.*";
    private final Pattern pattern = Pattern.compile(regexSalary);

    private List<JobDataModel> jobDataList = new ArrayList<>();

    @Override
    public List<JobDataModel> getRepositoryData() {
        return jobDataList;
    }

    @PostConstruct
    public void loadJobData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jobDataList = Arrays.asList(mapper.readValue(Paths.get("src/main/resources/data.json").toFile(), new TypeReference<>() {}));
        //additional information
        jobDataList.stream().forEach(job -> {
            Double salaryDoubleValue = parseSalary(job.getSalary().replaceAll(",",""));
            job.setDSalary(salaryDoubleValue);
        });
        logger.info("Job Data loaded successfully");
    }

    private Double parseSalary(String rawSalary) {
        Matcher matcher = pattern.matcher(rawSalary);
        if(matcher.find()) {
            Double result = Double.parseDouble(matcher.group(1));
            String suffixK = matcher.group(2);
            if(suffixK != null && suffixK.equalsIgnoreCase("k")) {
                result *= 1000;
            }
            if(rawSalary.contains("hr")) {
                result *= 720;
            }
            if(rawSalary.contains("yr")) {
                result /= 12;
            }
            return result;
        }
        return 0.0;
    }
}
