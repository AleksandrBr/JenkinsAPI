package by.epam.kronos.jenkinsApi.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import by.epam.kronos.jenkinsApi.entity.JenkinsJobDetails;
import by.epam.kronos.jenkinsApi.entity.JenkinsJobList;
import by.epam.kronos.jenkinsApi.entity.TestCasesFromSuite;
import by.epam.kronos.jenkinsApi.entity.TestSuiteFromJenkins;
import by.epam.kronos.jenkinsApi.job.PrepareReportBuilder;

public class JSOUBParser {

	public static final Logger log = LogManager.getLogger(PrepareReportBuilder.class);
	
	public static void parseHTMLAndAddResultToJenkinsJobList(InputStream iStream, String jobName, int duration) {

		
		JenkinsJobDetails jobDet = null;
		TestCasesFromSuite testCase = null;
		TestSuiteFromJenkins testSuite = null;
		String suiteNameTemp = null;
		Document doc;
		int countOfFailedTests = 0;
		int countOfFailedTestsInSuite = 0;

		try {
			doc = Jsoup.parse(iStream, "UTF-8", ""); //!!!????

			int totalTestsCount = doc.getElementsByAttributeValue("class", "test-name").size(); // total tests
			int countOfSkipedTests = doc.getElementsByAttributeValue("class", "test-status label right outline capitalize skip").size();	//count of skiped tests
			int countOfPassedTests = doc.getElementsByAttributeValue("class", "test-status label right outline capitalize pass").size();	//count of passed tests
			Elements links = doc.getElementsByTag("td");
			jobDet = new JenkinsJobDetails();
			jobDet.setJobName(jobName);
			jobDet.setTotalTestsCount(totalTestsCount);
			for (Element el : links) {
				for (Element errorLog : el.getElementsByAttributeValue("class", "step-details")) {
					for (int i = 0; i < errorLog.getElementsContainingText("with failure").size(); i++) {
						Element last = errorLog.getElementsContainingText("with failure").get(i);
						String suiteName;
						suiteName = last.parent().parent().parent().parent().parent()
								.getElementsByAttributeValue("class", "test-desc").get(0).child(1).text().substring(16); // suiteName(Package)
						if (testSuite == null || !suiteName.equals(suiteNameTemp)) {
							if (testSuite != null) {
								testSuite.setCountOfFailedTests(countOfFailedTestsInSuite);
								jobDet.addTestSuiteToList(testSuite);
							}
							testSuite = new TestSuiteFromJenkins();
							testSuite.setSuiteName(suiteName);
							suiteNameTemp = suiteName;
							countOfFailedTestsInSuite = 0;
						}																					// failed
						String testCaseName = last.parent().parent().parent().parent().parent().parent()	// test
								.getElementsByAttributeValue("class", "test-head").get(0).child(0).text();  // name															
						testCase = new TestCasesFromSuite();
						testCase.setTestCaseName(testCaseName);
						testCase.setErrorLog(last.text()); // Error Log
						testSuite.addTestCaseToList(testCase);
						countOfFailedTests++;
						countOfFailedTestsInSuite++;
					}
				}
			}
			if(testSuite != null)
			testSuite.setCountOfFailedTests(countOfFailedTestsInSuite);
			jobDet.setJobDuration(duration);
			jobDet.setCountOfFail(countOfFailedTests);
			jobDet.addTestSuiteToList(testSuite);
			jobDet.setCountOfSkip(countOfSkipedTests);
			jobDet.setCountOfPass(countOfPassedTests);
			JenkinsJobList.getInstance().addJenkinsJobToList(jobDet);
		} catch (IOException e) {
			log.info("Some unknown ERROR. Verify Connection Or Xpath : " + Arrays.toString(e.getStackTrace()));
		}
	}
}
