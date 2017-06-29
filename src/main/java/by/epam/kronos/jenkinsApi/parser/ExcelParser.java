package by.epam.kronos.jenkinsApi.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;

import by.epam.kronos.jenkinsApi.entity.JenkinsJobDetails;
import by.epam.kronos.jenkinsApi.entity.JenkinsJobList;
import by.epam.kronos.jenkinsApi.entity.TestCasesFromSuite;
import by.epam.kronos.jenkinsApi.entity.TestSuiteFromJenkins;
import by.epam.kronos.jenkinsApi.job.PrepareReportBuilder;
import by.epam.kronos.jenkinsApi.utils.ReportNameMaker;

public class ExcelParser {

	public static final Logger log = LogManager.getLogger(PrepareReportBuilder.class);
	
	private final static ExcelParser INSTANCE = new ExcelParser();
	private final static HSSFWorkbook workBook = new HSSFWorkbook();
	private HSSFSheet sheet;
	private HSSFRow row;
	private HSSFCellStyle rowStyle = null;
	private HSSFCellStyle persentageStyle = null;
	private File fileWithReport;
	private String titleSheet = "JobsReport";
	private int titleRow = 0;
	private int totalDuration = 0;

	public static ExcelParser getInstance() {
		return INSTANCE;
	}

	private ExcelParser() {
	}

	public void writeReportToExcel() {
			prepareForWriting();

		try (FileOutputStream writer = new FileOutputStream(createFile(ReportNameMaker.get()));){
			workBook.write(writer);
			workBook.close();
			writer.close();
		} catch (IOException e) {
			log.info("ERROR! Unknown Error! " + Arrays.toString(e.getStackTrace()));
		} 
	}

	private void prepareForWriting() {
			
		for (JenkinsJobDetails jobDetail : JenkinsJobList.getInstance().getJenkinsJobList()) {
			String checkName;
			if(jobDetail.getJobName().length() > 31){
			checkName = jobDetail.getJobName().substring((jobDetail.getJobName().length()-31), jobDetail.getJobName().length());}
			else{
				checkName = jobDetail.getJobName();
			}
			totalDuration = totalDuration + jobDetail.getJobDuration(); 
			if(workBook.getSheet(checkName)!=null){
				log.info("Sorry! But sheet with name: '" + checkName + "' alredy exist! Report for Job: '" + jobDetail.getJobName() + "' will not created! But Don't Be Worry Maybe You just Duplicated The Job Name");
				continue;
			}
				
			
			createSheet(titleSheet);
			createTitleHeader();												//This code
			createRow(titleRow);												//Create title sheet
			titleRow++;															// and write there 
			createCell(0).setCellValue(jobDetail.getJobName());					//Job Name & Count of
			createCell(1).setCellValue(jobDetail.getCountOfFail());				// failed tests
			createCell(2).setCellValue(jobDetail.getCountOfSkip());				// Skip
			createCell(3).setCellValue(jobDetail.getCountOfPass());				// Pass
			createCell(4).setCellValue(jobDetail.getTotalTestsCount());			//& total tests
			createCell(5).setCellValue(ReportNameMaker.durationConvert(jobDetail.getJobDuration()));
			
			if(jobDetail.getCountOfFail() == 0)
				continue;
			createSheet(checkName);
			createHeaderForJodDetailsSheet();												//
			createNextRow();																// THIS CODE
			createCell(0).setCellValue(jobDetail.getJobName());								//
			getCell(0).setCellStyle(getBoldFont());											// CREATE A NEW SHEET
			createCell(1).setCellValue(jobDetail.getCountOfFail());							//
																							//
			for (TestSuiteFromJenkins testSuite : jobDetail.getTestSuiteList()) {			//
				createNextRow();															// AND PREPARE 
				createNextRow();															// FULL INFORMATION
				createCell(0).setCellValue(testSuite.getSuiteName());						// ABOUT JENKINS JOB
				getCell(0).setCellStyle(getBoldFont());										//
				createCell(1).setCellValue(testSuite.getCountOfFailedTests());				//
																							//		
				for (TestCasesFromSuite testCase : testSuite.getTestCaseList()) {			//
																							//
					createNextRow();														//  FOR WRITE IT
					createCell(0).setCellValue(testCase.getTestCaseName());
					try{
					
					createCell(1).setCellValue(testCase.getErrorLog());	}
					catch(RuntimeException e){
						String s = testCase.getErrorLog();
						log.info("Some Error with STRING in ERROR LOG!!! Find The case error manualy here: " + testCase.getTestCaseName());
						createCell(1).setCellValue(testCase.getTestCaseName());	//
					}
				}
			}
		}
		
		
		createTitleFooter();
		
	}

	private HSSFCell createCell(int i) {
		return row.createCell(i);
	}

	private HSSFCell getCell(int i) {
		return row.getCell(i);
	}

	private HSSFRow createRow(int numOfRow) {
		row = sheet.createRow(numOfRow);
		return row;
	}

	private void createNextRow() {
		row = sheet.createRow(row.getRowNum() + 1);
	}

	private void createSheet(String jobName) {
		if (jobName.equals("JobsReport") && sheet != null) {
			sheet = workBook.getSheet(jobName);
			sheet.setColumnWidth(0, 19200); // 19200/256 = 75
		}
		else if(jobName.equals("JobsReport")){
			sheet = workBook.createSheet(jobName);
			sheet.setColumnWidth(0, 19200); // 19200/256 = 75
		}
		else{

			//jobName = jobName.substring((jobName.length()-31), jobName.length());
			sheet = workBook.createSheet(jobName);
			sheet.setColumnWidth(0, 19200); // 19200/256 = 75
			sheet.setColumnWidth(1, 19200); // 19200/256 = 75
		}
	}

	private void createTitleHeader(){		
		if(titleRow == 0){									
			createRow(titleRow);							
			createCell(0).setCellValue("Job Name");			//	THIS CODE 
			getCell(0).setCellStyle(getBoldFont());			//	CREATE A HEADER
			createCell(1).setCellValue("Failed");			//	FOR OUR TITLE
			getCell(1).setCellStyle(getBoldFont());			//	SHEET
			createCell(2).setCellValue("Skiped");			
			getCell(2).setCellStyle(getBoldFont());			
			createCell(3).setCellValue("Passed");			
			getCell(3).setCellStyle(getBoldFont());	
			createCell(4).setCellValue("Total");
			getCell(4).setCellStyle(getBoldFont());	
			createCell(5).setCellValue("Duration");
			getCell(5).setCellStyle(getBoldFont());	
			titleRow++;
		}		
	}
	
	private void createTitleFooter(){	
		createSheet(titleSheet);				
		createRow(titleRow);															//		
		createCell(0).setCellValue("Total Result");										//	Make a Total
		getCell(0).setCellStyle(getBoldFont()); 										//	 Result report		
		createCell(1).setCellFormula("SUM(B2:B"+row.getRowNum()+")");;	
		getCell(1).setCellStyle(getColorStyle("red"));		
		createCell(2).setCellFormula("SUM(C2:C"+row.getRowNum()+")");;	
		getCell(2).setCellStyle(getColorStyle("blue"));
		createCell(3).setCellFormula("SUM(D2:D"+row.getRowNum()+")");;	
		getCell(3).setCellStyle(getColorStyle("green"));
		createCell(4).setCellFormula("SUM(E2:E"+row.getRowNum()+")");
		getCell(4).setCellStyle(getBoldFont());
		createCell(5).setCellValue(ReportNameMaker.durationConvert(totalDuration));
		getCell(5).setCellStyle(getBoldFont());
		titleRow++; 																		
		createRow(titleRow);	
		createCell(0).setCellValue("Result in Percent");
		getCell(0).setCellStyle(getBoldFont());										//	 	
		createCell(1).setCellFormula("(B"+row.getRowNum()+"/E"+row.getRowNum()+")");// Make a Total
		getCell(1).setCellStyle(getPercentStyle());									// Result report
		createCell(2).setCellFormula("(C"+row.getRowNum()+"/E"+row.getRowNum()+")");// in percent
		getCell(2).setCellStyle(getPercentStyle());
		createCell(3).setCellFormula("(D"+row.getRowNum()+"/E"+row.getRowNum()+")");													
		getCell(3).setCellStyle(getPercentStyle());
		createCell(4).setCellValue(1);													
		getCell(4).setCellStyle(getPercentStyle());															
	}
	
	private void createHeaderForJodDetailsSheet(){
		createRow(0);
		createCell(0).setCellValue("JobName/SuiteName/TestCaseName");
		getCell(0).setCellStyle(getBoldFont());
		createCell(1).setCellValue("Failed/Error log");
		getCell(1).setCellStyle(getBoldFont());
	}
	
 	private HSSFCellStyle getBoldFont(){
		if(rowStyle == null){
		rowStyle = workBook.createCellStyle();
		HSSFFont font = workBook.createFont();
		font.setBold(true);
		rowStyle.setFont(font);
			}
		return rowStyle;
	}
	
	private HSSFCellStyle getColorStyle(String color){
	 HSSFCellStyle colorStyle = workBook.createCellStyle();
		HSSFFont font = workBook.createFont();
		if(color.toUpperCase().equals("RED"))
		font.setColor(Font.COLOR_RED);
		else if(color.toUpperCase().equals("GREEN"))
			font.setColor((short)3);
		else if(color.toUpperCase().equals("BLUE")){
			font.setColor((short)4);
		}
		else{
			font.setColor(Font.COLOR_NORMAL);
		}
		colorStyle.setFont(font);
		return colorStyle;
	}
	
	private HSSFCellStyle getPercentStyle(){
		if(persentageStyle == null){
		persentageStyle = workBook.createCellStyle();
		persentageStyle.setDataFormat(workBook.createDataFormat().getFormat("0.00%"));
			}
		return persentageStyle;
	}
	
	private File createFile(String fileName) {

		if (fileWithReport != null) {
			return fileWithReport;
		}
		fileWithReport = new File(fileName);
		return fileWithReport;
	}
	
}
