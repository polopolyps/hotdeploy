package com.polopoly.ps.hotdeploy.discovery;

import static com.polopoly.ps.hotdeploy.discovery.PlatformNeutralPath.unixToPlatformSpecificPath;
import junit.framework.TestCase;

public class FileConstants  {
    public static final String DEPENDED_TEST_JAR_DEPENDENCY_NAME = "discoverytestdependency";
    public static final String DEPENDED_TEST_JAR_FILE_NAME = DEPENDED_TEST_JAR_DEPENDENCY_NAME + "-1.0.jar";
    public static final String DEPENDED_TEST_JAR_PATH = unixToPlatformSpecificPath("repository/com/polopoly/hotdeploy/test/discoverytestdependency/1.0/" + DEPENDED_TEST_JAR_FILE_NAME);

    public static final String DEPENDING_TEST_JAR_DEPENDENCY_NAME = "discoverytest";
    public static final String DEPENDING_TEST_JAR_FILE_NAME = DEPENDING_TEST_JAR_DEPENDENCY_NAME + "-1.0.jar";
    public static final String DEPENDING_TEST_JAR_PATH = unixToPlatformSpecificPath("repository/com/polopoly/hotdeploy/test/discoverytest/1.0/" + DEPENDING_TEST_JAR_FILE_NAME);
}
