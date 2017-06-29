package by.epam.kronos.jenkinsApi.entity;

import java.util.ArrayList;
import java.util.List;

public class TestSuiteFromJenkins {
	
	private int countOfFailedTests;
	private boolean status;
	private String suiteName;
	private List<TestCasesFromSuite> testCaseList = new ArrayList<>();
	
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getSuiteName() {
		return suiteName;
	}
	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}
	public List<TestCasesFromSuite> getTestCaseList() {
		return testCaseList;
	}
	public void addTestCaseToList(TestCasesFromSuite testCaseList) {
		this.testCaseList.add(testCaseList);
	}
	public int getCountOfFailedTests() {
		return countOfFailedTests;
	}
	public void setCountOfFailedTests(int countOfFailedTests) {
		this.countOfFailedTests = countOfFailedTests;
	}
	
	
}
