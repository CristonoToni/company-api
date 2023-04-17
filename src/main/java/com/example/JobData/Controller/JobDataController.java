package com.example.JobData.Controller;

import com.example.JobData.Model.DataQueryParams;
import com.example.JobData.Service.IJobDataService;
import com.example.JobData.Exception.JobDataException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@Api(tags = "JobDataController", description = "Main Controller for Job API")
public class JobDataController {

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Autowired
	private IJobDataService service;

	@Value("${pagination.max.limit}")
	private int maxPageLimit;
	@Value("${pagination.default.limit}")
	private int defaultPageLimit;
	@Value("${pagination.default.offset}")
	private int defaultOffset;

	@GetMapping("v1/jobs")
	@ApiOperation(value = "Get Job information with support query parameters", notes = "query parameters are introduced as follows")
	public MappingJacksonValue getJobs(
			//filtering
			@ApiParam(value = "Search by Job title")
			@RequestParam(value = "job_title", required = false) String jobTitle,
			@ApiParam(value = "Search by Salary, with optional to comparable [gt,gte,lt,lte,eq]. For example, " +
					"salary=gte,20000 indicates to search which salary greater than equal 20000. Default is eq (equals to)")
			@RequestParam(value = "salary", required = false) String salary,
			@ApiParam(value = "Search by Gender")
			@RequestParam(value = "gender", required = false) String gender,
			//field selection
			@ApiParam(value = "Specify fields to show separated by comma. For example, field=job_title,salary,gender")
			@RequestParam(value = "field", required = false) String field,
			//sorting
			@ApiParam(value = "Sorting by specific fields. For example, sort=job_title,salary")
			@RequestParam(value = "sort", required = false) String sort,
			@ApiParam(value = "Sort type. the supported type is asc and desc. Default is asc")
			@RequestParam(value = "sort_type", required = false) String sortType,
			//pagination
			@ApiParam("Indicates the data size of each request with limit")
			@RequestParam(value = "limit", required = false, defaultValue = "${pagination.default.limit}") int limit,
			@ApiParam("Indicates which page of data to looking for by offset, start from offset=0")
			@RequestParam(value = "offset", required = false, defaultValue = "${pagination.default.offset}") int offset) throws JobDataException {

		DataQueryParams params = validateAndBuildQueryParams(jobTitle, salary, gender, field, sort, sortType, limit, offset);
		MappingJacksonValue mapping = service.getResult(params);
		return mapping;
	}

	private DataQueryParams validateAndBuildQueryParams(String jobTitle, String salary, String gender, String field,
														String sort, String sortType, int limit, int offset) throws JobDataException {
		DataQueryParams params = new DataQueryParams();

		params.setJobTitle(jobTitle);
		params.setGender(gender);

		int acceptLimit = limit > maxPageLimit ? maxPageLimit: (limit < 0 ? defaultPageLimit: limit);
		params.setLimit(acceptLimit);
		int acceptOffset = offset < 0 ? defaultOffset: offset;
		params.setOffset(acceptOffset);

		List<String> fields = Optional.ofNullable(field).map(f -> f.split(",")).stream().flatMap(Arrays::stream).collect(Collectors.toList());
		params.setFields(fields);

		List<String> sortFields = Optional.ofNullable(sort).map(f -> f.split(",")).stream().flatMap(Arrays::stream).collect(Collectors.toList());
		params.setSortFields(sortFields);

		String sortDirection = Optional.ofNullable(sortType).map(s -> s.equalsIgnoreCase("desc")? "desc" : "asc").orElse("asc");
		params.setSortType(sortDirection);

		try {
			//salary=gte,10000 or salary=10000
			if(salary != null) {
				String[] salarys = salary.split(",");
				if(salarys.length > 1) {
					params.setSalaryCmpSign(salarys[0].toLowerCase());
					params.setSalaryValue(Double.parseDouble(salarys[1]));
				} else {
					params.setSalaryCmpSign("eq");
					params.setSalaryValue(Double.parseDouble(salarys[0]));
				}
			}
		}catch (NumberFormatException err) {
			throw new JobDataException(err.getMessage());
		}

		return params;
	}
}
