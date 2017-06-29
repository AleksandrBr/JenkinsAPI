package by.epam.kronos.jenkinsApi.job;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.TestChildReport;

import by.epam.kronos.jenkinsApi.property.PropertyProvider;

public class PrepareReportBuilder {

	public static final Logger log = LogManager.getLogger(PrepareReportBuilder.class);

	private JenkinsServer jenkins = null;
	private SOAPReportBuilder soapReport;
	private NoSOAPReportBuilder noSoapReport;
	private static final String BASE_URL = PropertyProvider.getProperty("BASE_URL");

	public void startPrepearing(String jobName, String buildNumber){

		if (jenkins == null) {
			log.info("Try to find Jenkins Server by BASE URL: " + BASE_URL);
			jenkins = new JenkinsServer(URI.create(BASE_URL));
		}

			try {
				log.info("Try to find " + jobName);
				if(jenkins.getJob(jobName).getDownstreamProjects().size()!=0){
					log.info("This is a multi job '" + jobName + "' Go to find the result of each jobs");
					for(Job simpleJob: jenkins.getJob(jobName).getDownstreamProjects()){
						if(!simpleJob.getName().equals("KATE_Report_Builder"))
						makeReport(simpleJob.getName());
					}	
				}
				else{
					if(buildNumber!=null){
						makeReport(jobName, buildNumber);
					}
					else{
						makeReport(jobName);
					}
				}
			} catch (IOException e) {
					log.info("Something was wrong. Can't find the job name: '" + jobName + "' try it again /n"+Arrays.toString(e.getStackTrace()));
			}
	}

	private void makeReport(String jobName, String buildNumber) {
		
		try {
			int duration = (int)jenkins.getJob(jobName).getBuildByNumber(Integer.parseInt(buildNumber)).details().getDuration();
			// here we launch SOAP report by build number
			log.info("try to find job by Name " + jobName + "#" + buildNumber);
			List<TestChildReport> testReportList = jenkins.getJob(jobName)
					.getBuildByNumber(Integer.parseInt(buildNumber)).getTestReport().getChildReports();
			log.info("Go to SOAP Report Builder to prepare report");
			soapReport = new SOAPReportBuilder();
			soapReport.makeReport(jobName, buildNumber, testReportList, duration);
		} catch (IOException e1) { 												
			log.info("Go to NO_SOAP_UI Report Builder find job");					//
			noSoapReport = new NoSOAPReportBuilder();								// here we launch No SOAP Report
			noSoapReport.makeReportFromNotSOAPJob(jobName, buildNumber, jenkins);	//
		} catch (NullPointerException e1) {
			log.info("The Job Name: " + jobName + " Does Not exist:\n" + Arrays.toString(e1.getStackTrace()));
		}
	}
	
	private void makeReport(String jobName){
		
		try {
			int duration = (int)jenkins.getJob(jobName).getLastCompletedBuild().details().getDuration();
			// here we launch latest SOAP report
			log.info("try to find job by Name " + jobName);
			List<TestChildReport> testReportList = jenkins.getJob(jobName).getLastCompletedBuild().getTestReport()
					.getChildReports();
			log.info("Go to SOAP Report Builder to prepare report");
			soapReport = new SOAPReportBuilder();
			soapReport.makeReport(jobName, testReportList, duration);
		} catch (IOException e1) { 											
			log.info("Go to NO_SOAP_UI Report Builder find job");		//
			noSoapReport = new NoSOAPReportBuilder();					// here we launch No SOAP Report
			noSoapReport.makeReportFromNotSOAPJob(jobName, jenkins);	//
		} catch (NullPointerException e1) {
			log.info("The Job Name: " + jobName + " Does Not exist:\n" + Arrays.toString(e1.getStackTrace()));
		}
		
		
	}
	
}
