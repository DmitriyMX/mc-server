package ru.dmitriymx.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import ru.dmitriymx.gradle.extention.LogicExtention;

public class LogicPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getExtensions().create("logic", LogicExtention.class, project);
	}
}
