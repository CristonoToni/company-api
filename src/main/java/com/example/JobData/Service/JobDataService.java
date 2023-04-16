package com.example.JobData.Service;

import com.example.JobData.Model.APIResponse;
import com.example.JobData.Model.DataQueryParams;
import com.example.JobData.Model.JobDataModel;
import com.example.JobData.Repository.IJobDataRepository;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class JobDataService implements IJobDataService {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private IJobDataRepository repository;

    @Override
    public MappingJacksonValue getResult(DataQueryParams params) {
        List<JobDataModel> result = this.findAllWithParams(params);

        int limit = params.getLimit();
        int offset = params.getOffset();
        int startOffsetIndex = offset * limit;
        boolean hasMoreData = (offset+1) * limit < result.size();
        List<JobDataModel> pagedResult = result.stream().skip(startOffsetIndex).limit(limit).collect(Collectors.toList());

        SimpleBeanPropertyFilter filter = !params.getFields().isEmpty() ?
                SimpleBeanPropertyFilter.filterOutAllExcept(params.getFields().toArray(String[]::new)) : SimpleBeanPropertyFilter.serializeAll();
        FilterProvider filters = new SimpleFilterProvider().addFilter("JobDataFilter", filter);

        MappingJacksonValue mapping = new MappingJacksonValue(new APIResponse(HttpStatus.OK.value(), hasMoreData, pagedResult.size(), pagedResult));
        mapping.setFilters(filters);

        return mapping;
    }

    private List<JobDataModel> findAll() {
        return repository.getRepositoryData();
    }

    private List<JobDataModel> findAllWithParams(DataQueryParams params) {
        Predicate<JobDataModel> filterByParams = job -> {
            Optional<String> titleFilter = Optional.ofNullable(params.getJobTitle());
            Optional<String> genderFilter = Optional.ofNullable(params.getGender());
            Optional<String> salaryFilter = Optional.ofNullable(params.getSalaryCmpSign());
            boolean includeTitle = !titleFilter.isPresent() || job.getJobTitle().contains(titleFilter.get());
            boolean includeGender = !genderFilter.isPresent() || job.getGender().equalsIgnoreCase(genderFilter.get());
            boolean includeSalary = true;
            if(salaryFilter.isPresent()) {
                double jobSalary = job.getDSalary();
                switch (salaryFilter.get()) {
                    case "lt":
                        includeSalary = jobSalary < params.getSalaryValue();
                        break;
                    case "lte":
                        includeSalary = jobSalary <= params.getSalaryValue();
                        break;
                    case "gt":
                        includeSalary = jobSalary > params.getSalaryValue();
                        break;
                    case "gte":
                        includeSalary = jobSalary >= params.getSalaryValue();
                        break;
                    case "eq":
                        includeSalary = jobSalary == params.getSalaryValue();
                        break;
                    default:
                        break;
                }
            }

            return includeTitle && includeGender && includeSalary;
        };

        Comparator<JobDataModel> initComparator = null;
        for(String item: params.getSortFields()) {
            Comparator<JobDataModel> chainComparator = null;
            switch (item) {
                case "timestamp":
                    chainComparator = Comparator.comparing(JobDataModel::getTimestamp);
                    break;
                case "employer":
                    chainComparator = Comparator.comparing(JobDataModel::getEmployer);
                    break;
                case "location":
                    chainComparator = Comparator.comparing(JobDataModel::getLocation);
                    break;
                case "job_title":
                    chainComparator = Comparator.comparing(JobDataModel::getJobTitle);
                    break;
                case "years_at_employer":
                    chainComparator = Comparator.comparing(JobDataModel::getYearsatEmployer);
                    break;
                case "years_of_experience":
                    chainComparator = Comparator.comparing(JobDataModel::getYearsofExperience);
                    break;
                case "salary":
                    chainComparator = Comparator.comparing(JobDataModel::getDSalary);
                    break;
                case "signing_bonus":
                    chainComparator = Comparator.comparing(JobDataModel::getSigningBonus);
                    break;
                case "annual_bonus":
                    chainComparator = Comparator.comparing(JobDataModel::getAnnualBonus);
                    break;
                case "annual_stock_value":
                    chainComparator = Comparator.comparing(JobDataModel::getAnnualStockValue);
                    break;
                case "gender":
                    chainComparator = Comparator.comparing(JobDataModel::getGender);
                    break;
                case "additional_comments":
                    chainComparator = Comparator.comparing(JobDataModel::getAdditionalComments);
                    break;
                default:
                    break;
            }
            if(chainComparator != null){
                initComparator = initComparator == null? chainComparator: initComparator.thenComparing(chainComparator);
            }
        }

        List<JobDataModel> jobResult = this.findAll().stream().filter(filterByParams).collect(Collectors.toList());
        if(initComparator != null) {
            if (params.getSortType().equalsIgnoreCase("desc")) {
                initComparator = initComparator.reversed();
            }
            jobResult.sort(initComparator);
        }

        return jobResult;
    }
}
