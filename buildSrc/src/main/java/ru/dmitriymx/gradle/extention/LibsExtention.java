package ru.dmitriymx.gradle.extention;

import java.util.List;

public class LibsExtention {

	public final String lombok = "org.projectlombok:lombok:1.18.12";
	public final String annotations = "com.google.code.findbugs:jsr305:3.0.2";
	public final String guava = "com.google.guava:guava:30.1-jre";
	public final String lang3 = "org.apache.commons:commons-lang3:3.11";

	public final LoggerLibs logger = new LoggerLibs();
	public final Dagger2Libs dagger2 = new Dagger2Libs();
	public final Junit5Libs junit5 = new Junit5Libs();

	public static final class LoggerLibs {
		private final String slf4j_version = "1.7.30";
		private final String logback_version = "1.2.3";

		public final List<String> slf4j = List.of(
				"org.slf4j:slf4j-api:" + slf4j_version,
				"org.slf4j:jcl-over-slf4j:" + slf4j_version
		);

		public final List<String> logback = List.of(
				"ch.qos.logback:logback-core:" + logback_version,
				"ch.qos.logback:logback-classic:" + logback_version
		);
	}

	public static final class Dagger2Libs {
		private final String dagger2_version = "2.33";

		public final String implementation = "com.google.dagger:dagger:" + dagger2_version;
		public final String annotationProcessor = "com.google.dagger:dagger-compiler:" + dagger2_version;
	}

	public static final class Junit5Libs {
		private final String junit_version = "5.5.2";

		public final String api = "org.junit.jupiter:junit-jupiter-api:" + junit_version;
		/** runtimeOnly */
		public final String engine = "org.junit.jupiter:junit-jupiter-engine:" + junit_version;
		public final String params = "org.junit.jupiter:junit-jupiter-params:" + junit_version;
	}
}
