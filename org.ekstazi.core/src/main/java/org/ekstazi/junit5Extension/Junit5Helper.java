package org.ekstazi.junit5Extension;

public class Junit5Helper {
    protected static Boolean isTestFailed = false;

    /** Test name that used for getting all configuration values */
    private final static String configTestName = "TestGetConfigValueForConfigAware";

    public static Boolean isTestClassTransformNeeded (String className) {
        if (className.contains("Test") &&
                !className.contains(configTestName) &&
                !className.contains("org/apache/tools/ant") &&
                !className.contains("org/apache/maven") &&
                !className.contains("org/junit") &&
                !className.contains("org/opentest4j") &&
                !className.contains("org/ekstazi")) {
            return true;
        }
        return false;
    }
}
