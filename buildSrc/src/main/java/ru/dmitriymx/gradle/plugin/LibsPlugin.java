package ru.dmitriymx.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import ru.dmitriymx.gradle.extention.LibsExtention;

public class LibsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getExtensions().create("libs", LibsExtention.class);
	}
}
