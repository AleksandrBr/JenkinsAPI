package by.epam.kronos.jenkinsApi.entity;

import java.util.ArrayList;
import java.util.List;

public class JenkinsJobDetails {
	
	private int countOfPass;
	private int countOfSkip;
	private int totalTestsCount;
	private int countOfFail;
	private int jobDuration;
	private String jobName;
	private List<TestSuiteFromJenkins> testSuiteList = new ArrayList<>();

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public List<TestSuiteFromJenkins> getTestSuiteList() {
		return testSuiteList;
	}

	public void addTestSuiteToList(TestSuiteFromJenkins testSuiteList) {
		this.testSuiteList.add(testSuiteList);
	}

	public int getCountOfFail() {
		return countOfFail;
	}

	public void setCountOfFail(int countOfFail) {
		this.countOfFail = countOfFail;
	}

	public int getTotalTestsCount() {
		return totalTestsCount;
	}

	public void setTotalTestsCount(int totalTestsCount) {
		this.totalTestsCount = totalTestsCount;
	}

	public int getCountOfPass() {
		return countOfPass;
	}

	public void setCountOfPass(int countOfPass) {
		this.countOfPass = countOfPass;
	}

	public int getCountOfSkip() {
		return countOfSkip;
	}

	public void setCountOfSkip(int countOfSkip) {
		this.countOfSkip = countOfSkip;
	}

	public int getJobDuration() {
		return jobDuration;
	}

	public void setJobDuration(int duration) {
		this.jobDuration = duration;
	}

	
}
