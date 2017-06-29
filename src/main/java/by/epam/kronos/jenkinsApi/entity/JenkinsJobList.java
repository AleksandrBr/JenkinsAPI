package by.epam.kronos.jenkinsApi.entity;

import java.util.ArrayList;
import java.util.List;

public class JenkinsJobList {

	private static final JenkinsJobList INSTANCE = new JenkinsJobList();

	private List<JenkinsJobDetails> jenkinsJobList = new ArrayList<>();
	
	private JenkinsJobList() {
	}

	public static JenkinsJobList getInstance() {
		return INSTANCE;
	}


	public List<JenkinsJobDetails> getJenkinsJobList() {
		return jenkinsJobList;
	}

	public void addJenkinsJobToList(JenkinsJobDetails jenkinsJobList) {
		this.jenkinsJobList.add(jenkinsJobList);
	}

}
