package by.epam.kronos.jenkinsApi.job;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.offbytwo.jenkins.model.TestCase;
import com.offbytwo.jenkins.model.TestChildReport;
import com.offbytwo.jenkins.model.TestSuites;

import by.epam.kronos.jenkinsApi.entity.JenkinsJobDetails;
import by.epam.kronos.jenkinsApi.entity.JenkinsJobList;
import by.epam.kronos.jenkinsApi.entity.TestCasesFromSuite;
import by.epam.kronos.jenkinsApi.entity.TestSuiteFromJenkins;
import by.epam.kronos.jenkinsApi.property.PropertyProvider;

public class SOAPReportBuilder {

	public static final Logger log = LogManager.getLogger(PrepareReportBuilder.class);
	private static final String CASE_FOR_SKIP = PropertyProvider.getProperty("TESTS_FOR_SKIP");
	private static final String BASE_URL = PropertyProvider.getProperty("BASE_URL");
	private JenkinsJobDetails jobDetails = new JenkinsJobDetails();
	private TestCasesFromSuite testCaseDetail;
	private TestSuiteFromJenkins testSuiteDetail;
	private int allTestsFailedInJob = 0;
	private int totalJobTests = 0;
	private int countOfSkiped = 0;
	// private int countOfPassedTests;
	private boolean isSkipSuitePresent = false;

	public void makeReport(String jobName, List<TestChildReport> testReportList, int duration) {
		
		makeAllJobPlease(jobName, testReportList);

		jobDetails.setJobName(jobName);
		jobDetails.setCountOfFail(allTestsFailedInJob);
		if (allTestsFailedInJob == 0) {
			jobDetails.setJobName(jobName);
		}
		jobDetails.setCountOfPass(totalJobTests - (countOfSkiped + allTestsFailedInJob));
		jobDetails.setJobDuration(duration);
		JenkinsJobList.getInstance().addJenkinsJobToList(jobDetails);
	}

	public void makeReport(String jobName, String buildNumber, List<TestChildReport> testReportList, int duration) {

		makeAllJobPlease(jobName, testReportList);

		jobDetails.setJobName(jobName + " #" + buildNumber);
		jobDetails.setCountOfFail(allTestsFailedInJob);
		if (allTestsFailedInJob == 0) {
			jobDetails.setJobName(jobName + " #" + buildNumber);
		}

		jobDetails.setCountOfPass(totalJobTests - (countOfSkiped + allTestsFailedInJob));
		jobDetails.setJobDuration(duration);
		JenkinsJobList.getInstance().addJenkinsJobToList(jobDetails);

	}

	private void getCountOfAllTestsInJob(TestChildReport testReport) {
		
		for (TestSuites testSuite : testReport.getResult().getSuites()) {
			if (checkSuiteForSkip(testSuite)) {
				continue;
			}
			for (TestCase caseCount : testSuite.getCases()) {
				if (checkCaseForSkip(caseCount))
					continue;
				if (caseCount.isSkipped()) {
					countOfSkiped++;
				}
				// if(caseCount.getStatus().equals("PASSED")){ //
				// countOfPassedTests++; // it will work when all step will have
				// assertion
				// } //
				totalJobTests++;
			}
		}
		// jobDetails.setCountOfPass(countOfPassedTests);
		jobDetails.setCountOfSkip(countOfSkiped);
		jobDetails.setTotalTestsCount(totalJobTests);
	}

	private void makeAllJobPlease(String jobName, List<TestChildReport> testReportList) {
		
		for (TestChildReport testReport : testReportList) {
			try {
				getCountOfAllTestsInJob(testReport); // count of all and count
														// of skiped adding to
														// JobDetails
			} catch (RuntimeException e) {
				log.info("Broken Job Report Please Look Details here: " + BASE_URL + "/job/" + jobName);
				log.info(Arrays.toString(e.getStackTrace()));
				break;
			}
			if (testReport.getResult().getFailCount() == 0) {
				continue;
			}
			for (TestSuites testSuite : testReport.getResult().getSuites()) {																			
				if (checkSuiteForSkip(testSuite)) {											
					continue; 																	
				}
				int count = 0;
				testSuiteDetail = new TestSuiteFromJenkins();
				for (TestCase testCase : testSuite.getCases()) {
					testCaseDetail = null;
					if (checkCaseForSkip(testCase)) {											
						continue; 																	
					}
					if (testCase.getErrorDetails() == null) {
						continue;
					} else {
						testCaseDetail = new TestCasesFromSuite();
						testCaseDetail.setTestCaseName(testCase.getName());
						if(testCase.getErrorStackTrace().length() < 32000)
						testCaseDetail.setErrorLog(testCase.getErrorStackTrace());
						else	testCaseDetail.setErrorLog(testCase.getErrorDetails());
						count++;
						allTestsFailedInJob++;
						testSuiteDetail.addTestCaseToList(testCaseDetail);
					}
				}
				if (count != 0) {
					testSuiteDetail.setSuiteName(testSuite.getName());
					testSuiteDetail.setCountOfFailedTests(count);
					jobDetails.addTestSuiteToList(testSuiteDetail);
				}
			}
			
		}

	}

	private boolean checkSuiteForSkip(TestSuites testSuite){
		isSkipSuitePresent=false;
		if(!CASE_FOR_SKIP.equals("None")){
		for (String checkName : CASE_FOR_SKIP.split("/")) {
			if (testSuite.getName().toUpperCase().contains(checkName.toUpperCase())) { // This code
				isSkipSuitePresent = true; 												// check the suite name
				break; 																	// and skip it
			} 																			// if this name
		}}								 												// contains in property file		
		return isSkipSuitePresent;
	}
	
	private boolean checkCaseForSkip(TestCase testCase){
		isSkipSuitePresent = false;
		if(!CASE_FOR_SKIP.equals("None")){
		for (String checkName : CASE_FOR_SKIP.split("/")) {
			if (testCase.getName().toUpperCase().contains(checkName.toUpperCase())) { 	// This code
				isSkipSuitePresent = true; 												// check the testCase name
				break; 																	// and skip it
			} 																			// if this name
		}} 																				// contains in property file
		return isSkipSuitePresent;
	}
}
