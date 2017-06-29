package by.epam.kronos.jenkinsApi.utils;

import java.util.Calendar;

import by.epam.kronos.jenkinsApi.property.PropertyProvider;

public class ReportNameMaker {

	public static String get() {

		Calendar cal = Calendar.getInstance();

		int dayOfMonth = cal.get((Calendar.DAY_OF_MONTH));
		int year = cal.get((Calendar.YEAR));
		int month = cal.get((Calendar.MONTH))+1;
		int hours = cal.get((Calendar.HOUR_OF_DAY));
		int minutes = cal.get((Calendar.MINUTE));
		int second = cal.get((Calendar.SECOND));

		String reportFileName = PropertyProvider.getProperty("FILE_REPORT_NAME") + String.valueOf(dayOfMonth) + "_"
				+ String.valueOf(month) + "_" + String.valueOf(year) + "T" + String.valueOf(hours) + "_"
				+ String.valueOf(minutes) + "_" + String.valueOf(second) + ".xls";

		return reportFileName;

	}
	
	public static String durationConvert(int duration){
		int hours = duration/3600000;
		int minutes = duration/60000 - hours*60;
		int sec = duration/1000 - hours*3600 - minutes*60;
		int mSec = duration%1000;
		return (new String(hours+"h:"+minutes+"m:"+sec+"s:"+mSec+"ms"));
	}

}
