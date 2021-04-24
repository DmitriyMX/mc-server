package ru.dmitriymx.gradle.extention;

import java.util.List;

public class LibsExtention {

	public final String lombok = "org.projectlombok:lombok:1.18.12";
	public final String annotations = "com.google.code.findbugs:jsr305:3.0.2";
	public final LoggerLibs logger = new LoggerLibs();
	public final Dagger2Libs dagger2 = new Dagger2Libs();

	public static final class LoggerLibs {
		private final String slf4j_version = "1.7.30";
		private final String logback_version = "1.2.3";

		public final List<String> slf4j = List.of(
				"org.slf4j:slf4j-api:" + slf4j_version,
				"org.slf4j:jcl-over-slf4j:" + slf4j_version
		);
		public final String slf4j_simple = "org.slf4j:slf4j-simple:" + slf4j_version;

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
}
