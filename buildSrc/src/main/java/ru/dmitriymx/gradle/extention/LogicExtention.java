package ru.dmitriymx.gradle.extention;

import org.gradle.api.Project;

public class LogicExtention {
	private final Project project;

	public LogicExtention(Project project) {
		this.project = project;
	}

	public String getProperty1(String propertyName1, String propertyName2) {
		return (String) (project.hasProperty(propertyName1) ? project.property(propertyName1) : project.property(propertyName2));
	}

	public String getProperty1(String propertyName) {
		return (String) (project.hasProperty(propertyName) ? project.property(propertyName) : null);
	}
}
