package com.example.JobData.Model;

import java.io.Serializable;

import com.example.JobData.Util.DoubleSerializer;
import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonFilter("JobDataFilter")
public class JobDataModel implements Serializable {
	private String Timestamp;
	private String JobTitle;
	private String Employer;
	private String Location;
	private String YearsatEmployer;
	private String YearsofExperience;
	private String Salary;
	private String SigningBonus;
	private String AnnualBonus;
	private String AnnualStockValue;
	private String Gender;
	private String AdditionalComments;

	private Double DSalary;

	/**
	 * Getter fields
	 */
	@JsonProperty("timestamp")
	public String getTimestamp() {
		return Timestamp;
	}
	@JsonProperty("job_title")
	public String getJobTitle() {
		return JobTitle;
	}
	@JsonProperty("employer")
	public String getEmployer() {
		return Employer;
	}
	@JsonProperty("location")
	public String getLocation() {
		return Location;
	}
	@JsonProperty("years_at_employer")
	public String getYearsatEmployer() {
		return YearsatEmployer;
	}
	@JsonProperty("years_of_experience")
	public String getYearsofExperience() {
		return YearsofExperience;
	}
	@JsonProperty("salary_raw")
	@JsonIgnore
	public String getSalary() {
		return Salary;
	}
	@JsonProperty("signing_bonus")
	public String getSigningBonus() {
		return SigningBonus;
	}
	@JsonProperty("annual_bonus")
	public String getAnnualBonus() {
		return AnnualBonus;
	}
	@JsonProperty("annual_stock_bonus")
	public String getAnnualStockValue() {
		return AnnualStockValue;
	}
	@JsonProperty("gender")
	public String getGender() {
		return Gender;
	}
	@JsonProperty("additional_comments")
	public String getAdditionalComments() {
		return AdditionalComments;
	}
	//Additional
	@JsonProperty("salary")
	@JsonSerialize(using = DoubleSerializer.class)
	public Double getDSalary() {
		return DSalary;
	}

	/**
	 * Setter fields
	 */
	@JsonProperty("Timestamp")
	public void setTimestamp(String timestamp) {
		Timestamp = timestamp;
	}
	@JsonProperty("Job Title")
	public void setJobTitle(String jobTitle) {
		JobTitle = jobTitle;
	}
	@JsonProperty("Employer")
	public void setEmployer(String employer) {
		Employer = employer;
	}
	@JsonProperty("Location")
	public void setLocation(String location) {
		Location = location;
	}
	@JsonProperty("Years at Employer")
	public void setYearsatEmployer(String yearsatEmployer) {
		YearsatEmployer = yearsatEmployer;
	}
	@JsonProperty("Years of Experience")
	public void setYearsofExperience(String yearsofExperience) {
		YearsofExperience = yearsofExperience;
	}
	@JsonProperty("Salary")
	public void setSalary(String salary) {
		Salary = salary;
	}
	@JsonProperty("Signing Bonus")
	public void setSigningBonus(String signingBonus) {
		SigningBonus = signingBonus;
	}
	@JsonProperty("Annual Bonus")
	public void setAnnualBonus(String annualBonus) {
		AnnualBonus = annualBonus;
	}
	@JsonProperty("Annual Stock Value/Bonus")
	public void setAnnualStockValue(String annualStockValue) {
		AnnualStockValue = annualStockValue;
	}
	@JsonProperty("Gender")
	public void setGender(String gender) {
		Gender = gender;
	}
	@JsonProperty("Additional Comments")
	public void setAdditionalComments(String additionalComments) {
		AdditionalComments = additionalComments;
	}
	//Additional
	public void setDSalary(Double DSalary) {
		this.DSalary = DSalary;
	}
}