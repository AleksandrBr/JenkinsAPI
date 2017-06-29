package by.epam.kronos.jenkinsApi.property;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
//import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.epam.kronos.jenkinsApi.job.PrepareReportBuilder;

public class PropertyProvider {

	public static final Logger log = LogManager.getLogger(PrepareReportBuilder.class);
	private static final String CONFIG_PATH = "prop.properties";
	/*
	 * private static final ResourceBundle bundle =
	 * ResourceBundle.getBundle(CONFIG_PATH);
	 * 
	 * public static String getProperty(String propertyName) { return
	 * bundle.getString(propertyName); }
	 * 
	 */
	static Properties prop = new Properties();
	static InputStream input = null;

	public static String getProperty(String propertyName) {
		try {
			input = new FileInputStream(CONFIG_PATH);
			prop.load(input);
			return prop.getProperty(propertyName);

		} catch (FileNotFoundException e) {
			log.info("Properties file does not found: " + Arrays.toString(e.getStackTrace()));
			return null;
		} catch (IOException e) {
			log.info("Property file does not contains your key : " + propertyName + "\n"
					+ Arrays.toString(e.getStackTrace()));
			return null;
		}
	}

}
