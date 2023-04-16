package com.example.demo;

import com.example.JobData.JobDataApplication;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = JobDataApplication.class)
@AutoConfigureMockMvc
class JobDataApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testGetJobs_no_params() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andExpect(jsonPath("$.data[0].salary").exists())
				.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		boolean has_more = JsonPath.read(responseBody, "$.has_more");
		int count = JsonPath.read(responseBody, "$.data_count");
		String title = JsonPath.read(responseBody, "$.data[0].job_title");
		assertThat(has_more, is(true));
		assertThat(count, is(equalTo(50)));
		assertThat(title, notNullValue());
	}

	@Test
	void testGetJobs_param_salary_no_sign() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("salary", "10280000"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		boolean has_more = JsonPath.read(responseBody, "$.has_more");
		String salary = JsonPath.read(responseBody, "$.data[0].salary");
		int count =  JsonPath.read(responseBody, "$.data_count");
		assertThat(has_more, is(false));
		assertThat(salary, is(equalTo("10280000.0"))); //eq by default
		assertThat(count, is(1));
	}

	@Test
	void testGetJobs_param_salary_with_sign() throws Exception {
		//gte
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("salary", "gte,200000000")
						.param("sort", "salary")
						.param("gender", "male"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		boolean has_more = JsonPath.read(responseBody, "$.has_more");
		int count =  JsonPath.read(responseBody, "$.data_count");
		String salary_1 = JsonPath.read(responseBody, "$.data[0].salary");
		String salary_2 = JsonPath.read(responseBody, "$.data[1].salary");
		assertThat(has_more, is(false));
		assertThat(count, is(2));
		assertThat(salary_1, is(equalTo("200000000.0")));
		assertThat(salary_2, is(equalTo("100000000000003.0")));

		//gt
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("salary", "gt,200000000")
						.param("sort", "salary")
						.param("gender", "male"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();

		responseBody = mvcResult.getResponse().getContentAsString();
		has_more = JsonPath.read(responseBody, "$.has_more");
		count =  JsonPath.read(responseBody, "$.data_count");
		salary_1 = JsonPath.read(responseBody, "$.data[0].salary");
		assertThat(has_more, is(false));
		assertThat(count, is(1));
		assertThat(salary_1, not(equalTo("200000000.0")));

		//eq
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("salary", "eq,200000000")
						.param("sort", "salary")
						.param("gender", "male"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();

		responseBody = mvcResult.getResponse().getContentAsString();
		has_more = JsonPath.read(responseBody, "$.has_more");
		count =  JsonPath.read(responseBody, "$.data_count");
		salary_1 = JsonPath.read(responseBody, "$.data[0].salary");
		assertThat(has_more, is(false));
		assertThat(count, is(1));
		assertThat(salary_1, is(equalTo("200000000.0")));

		//lte
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("salary", "lte,200000000")
						.param("sort", "salary")
						.param("sort_type", "desc")
						.param("gender", "male"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();

		responseBody = mvcResult.getResponse().getContentAsString();
		has_more = JsonPath.read(responseBody, "$.has_more");
		salary_1 = JsonPath.read(responseBody, "$.data[0].salary");
		count =  JsonPath.read(responseBody, "$.data_count");
		assertThat(has_more, is(true));
		assertThat(count, is(greaterThan(1)));
		assertThat(salary_1, is(equalTo("200000000.0")));

		//lt
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("salary", "lt,200000000")
						.param("sort", "salary")
						.param("sort_type", "desc")
						.param("gender", "male"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();

		responseBody = mvcResult.getResponse().getContentAsString();
		has_more = JsonPath.read(responseBody, "$.has_more");
		salary_1 = JsonPath.read(responseBody, "$.data[0].salary");
		count =  JsonPath.read(responseBody, "$.data_count");
		assertThat(has_more, is(true));
		assertThat(count, is(greaterThan(1)));
		assertThat(salary_1, not(equalTo("200000000.0")));
	}

	@Test
	void testGetJobs_param_jobTitle() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("job_title", "Developer"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString();
		String title = JsonPath.read(responseBody, "$.data[0].job_title");
		assertThat(title, containsString("Developer"));


		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("job_title", "Java Software Developer"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		responseBody = mvcResult.getResponse().getContentAsString();
		title = JsonPath.read(responseBody, "$.data[0].job_title");
		int count = JsonPath.read(responseBody, "$.data_count");
		assertThat(count, is(1));
		assertThat(title, equalTo("Java Software Developer"));
	}

	@Test
	void testGetJobs_param_gender() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("gender", "male"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString();
		String gender = JsonPath.read(responseBody, "$.data[0].gender");
		boolean has_more = JsonPath.read(responseBody, "$.has_more");
		assertThat(has_more, is(true));
		assertThat(gender, equalTo("Male"));

		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("gender", "unidentified"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		responseBody = mvcResult.getResponse().getContentAsString();
		int count = JsonPath.read(responseBody, "$.data_count");
		has_more = JsonPath.read(responseBody, "$.has_more");
		assertThat(has_more, is(false));
		assertThat(count, is(0));
	}

	@Test
	void testGetJobs_param_sort_with_type() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("sort", "salary,job_title"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString();
		String salary = JsonPath.read(responseBody, "$.data[0].salary");
		boolean has_more = JsonPath.read(responseBody, "$.has_more");
		assertThat(has_more, is(true));
		assertThat(salary, equalTo("0.0")); //asc by default = min first

		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("sort", "salary,job_title")
						.param("sort_type", "desc"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		responseBody = mvcResult.getResponse().getContentAsString();
		salary = JsonPath.read(responseBody, "$.data[0].salary");
		has_more = JsonPath.read(responseBody, "$.has_more");
		assertThat(has_more, is(true));
		assertThat(salary, equalTo("100000000000003.0")); //desc = max first
	}

	@Test
	void testGetJobs_param_field_selections() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("field", "salary,job_title"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andExpect(jsonPath("$.data[0].salary").exists())
				.andExpect(jsonPath("$.data[0].job_title").exists())
				.andExpect(jsonPath("$.data[0].gender").doesNotExist())
				.andExpect(jsonPath("$.data[0].location").doesNotExist())
				.andExpect(jsonPath("$.data[0].timestamp").doesNotExist())
				.andExpect(jsonPath("$.data[1].salary").exists())
				.andExpect(jsonPath("$.data[1].job_title").exists())
				.andExpect(jsonPath("$.data[1].gender").doesNotExist())
				.andExpect(jsonPath("$.data[1].location").doesNotExist())
				.andExpect(jsonPath("$.data[1].timestamp").doesNotExist());
	}

	@Value("${pagination.max.limit}")
	private int capLimit;
	@Value("${pagination.default.limit}")
	private int defaultLimit;

	@Test
	void testGetJobs_param_pagination() throws Exception {
		//limit
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("limit", "100"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString();
		int count = JsonPath.read(responseBody, "$.data_count");
		boolean has_more = JsonPath.read(responseBody, "$.has_more");
		assertThat(has_more, is(true));
		assertThat(count, equalTo(100));

		//default limit
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		responseBody = mvcResult.getResponse().getContentAsString();
		count = JsonPath.read(responseBody, "$.data_count");
		has_more = JsonPath.read(responseBody, "$.has_more");
		assertThat(has_more, is(true));
		assertThat(count, equalTo(defaultLimit));

		//exceed cap limit -> max limit
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("limit", "1000000"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		responseBody = mvcResult.getResponse().getContentAsString();
		count = JsonPath.read(responseBody, "$.data_count");
		has_more = JsonPath.read(responseBody, "$.has_more");
		assertThat(has_more, is(true));
		assertThat(count, equalTo(capLimit));

		//invalid limit -> default limit
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("limit", "-1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		responseBody = mvcResult.getResponse().getContentAsString();
		count = JsonPath.read(responseBody, "$.data_count");
		has_more = JsonPath.read(responseBody, "$.has_more");
		assertThat(has_more, is(true));
		assertThat(count, equalTo(defaultLimit));

		//offset
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("offset", "0")
						.param("sort", "job_title"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		responseBody = mvcResult.getResponse().getContentAsString();
		has_more = JsonPath.read(responseBody, "$.has_more");
		String title = JsonPath.read(responseBody, "$.data[0].job_title");
		assertThat(has_more, is(true));
		assertThat(title, containsString(""));

		//default offset
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("sort", "job_title"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		responseBody = mvcResult.getResponse().getContentAsString();
		has_more = JsonPath.read(responseBody, "$.has_more");
		title = JsonPath.read(responseBody, "$.data[0].job_title");
		assertThat(has_more, is(true));
		assertThat(title, containsString(""));

		//exceed offset
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("offset", "1000")
						.param("sort", "job_title"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		responseBody = mvcResult.getResponse().getContentAsString();
		has_more = JsonPath.read(responseBody, "$.has_more");
		count = JsonPath.read(responseBody, "$.data_count");
		assertThat(has_more, is(false));
		assertThat(count, is(0));

		//invalid offset -> default offset
		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/jobs")
						.param("offset", "-1")
						.param("sort", "job_title"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andReturn();
		responseBody = mvcResult.getResponse().getContentAsString();
		has_more = JsonPath.read(responseBody, "$.has_more");
		title = JsonPath.read(responseBody, "$.data[0].job_title");
		assertThat(has_more, is(true));
		assertThat(title, containsString(""));
	}


}
