package com.syhan.javatool.share.config;

import java.io.File;

public abstract class ProjectSources {
    //
    public static final String PATH_DELIM = File.separator;
    public static final String SRC_MAIN_JAVA      = String.format("src%smain%sjava", PATH_DELIM, PATH_DELIM);          // src/main/java
    public static final String SRC_MAIN_RESOURCES = String.format("src%smain%sresources", PATH_DELIM, PATH_DELIM);     // src/main/resources
    public static final String SRC_TEST_JAVA      = String.format("src%stest%sjava", PATH_DELIM, PATH_DELIM);          // src/test/java
    public static final String SRC_TEST_RESOURCES = String.format("src%stest%sresources", PATH_DELIM, PATH_DELIM);     // src/test/resources

    // C://Users/user/Documents/.../src/main/java/com/foo/bar/SampleService.java -> com/foo/bar/SampleService.java
    public static String extractSourceFilePath(String physicalSourceFilePath) {
        //
        int sourceFilePathIndex = computeSourceFilePathIndex(physicalSourceFilePath);

        if (sourceFilePathIndex < 0) {
            throw new RuntimeException("physicalSourceFilePath is not correct! : " + physicalSourceFilePath);
        }

        return physicalSourceFilePath.substring(sourceFilePathIndex);
    }

    private static int computeSourceFilePathIndex(String physicalSourceFilePath) {
        // if it contains src/main/java (ex ./source-project/src/main/java/foo/bar/Sample.java)
        int index = physicalSourceFilePath.indexOf(SRC_MAIN_JAVA);
        if (index >= 0) {
            return index + SRC_MAIN_JAVA.length() + 1;
        }

        // if it contains src/main/resources (ex ./source-project/src/main/resources/foo/bar/SampleSqlMap.xml)
        index = physicalSourceFilePath.indexOf(SRC_MAIN_RESOURCES);
        if (index >= 0) {
            return index + SRC_MAIN_RESOURCES.length() + 1;
        }

        return -1;
    }
}
