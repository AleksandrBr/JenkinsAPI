package by.epam.kronos.jenkinsApi.app;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import by.epam.kronos.jenkinsApi.entity.JenkinsJobList;
import by.epam.kronos.jenkinsApi.job.PrepareReportBuilder;
import by.epam.kronos.jenkinsApi.parser.ExcelParser;
import by.epam.kronos.jenkinsApi.property.PropertyProvider;
import by.epam.kronos.jenkinsApi.utils.ReportNameMaker;

public class Demo {
	public static final Logger log = LogManager.getLogger(PrepareReportBuilder.class);
	private static PrepareReportBuilder jr = new PrepareReportBuilder();
	private static final String JOB_NAMES = PropertyProvider.getProperty("JOB_NAMES");
	private static String jobName;
	private static String buildNumber;

	public static void main(String[] args) {

		String[] lines = JOB_NAMES.split(","); 
		for (String currentJobName : lines) {
			String[] jobList = null;
			if (currentJobName.contains("#")) {
				jobList = currentJobName.split("#");
			}
			if (jobList != null) {
				jobName = jobList[0];
				buildNumber = jobList[1];
				jr.startPrepearing(jobName, buildNumber);
			} else {
				jobName = currentJobName;
				buildNumber = null;
				jr.startPrepearing(jobName, buildNumber);
			}
		}
		try{
		if (JenkinsJobList.getInstance().getJenkinsJobList().isEmpty()) {
			log.info("Application Error. Information About jobs is Empty. Can't Run Excel Parser.\nApplication Closed");
		} else {
			log.info("Start writing the result to Excel file: " + ReportNameMaker.get());
			ExcelParser.getInstance().writeReportToExcel();
			log.info("Application close");
		}}
		catch(RuntimeException e){
			log.info("UNKNOWN ERROR! CHECK THE ALL PARAMETERS IN JOB OR YOU CONNECTION WITH KATE!" +Arrays.toString(e.getStackTrace()));
		}
		
	}

}
