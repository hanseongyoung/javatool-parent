package com.syhan.javatool.share.config;

import java.io.File;

public class ProjectConfiguration {
    //
    private static final String PATH_DELIM = File.separator;
    private static final String SRC_MAIN_JAVA      = String.format("src%smain%sjava", PATH_DELIM, PATH_DELIM);          // src/main/java
    private static final String SRC_MAIN_RESOURCES = String.format("src%smain%sresources", PATH_DELIM, PATH_DELIM);     // src/main/resources
    private static final String SRC_TEST_JAVA      = String.format("src%stest%sjava", PATH_DELIM, PATH_DELIM);          // src/test/java
    private static final String SRC_TEST_RESOURCES = String.format("src%stest%sresources", PATH_DELIM, PATH_DELIM);     // src/test/resources

    private final ConfigurationType type;
    private final String projectHomePath;

    private final String physicalJavaPath;
    private final String physicalResourcesPath;
    private final String physicalTestPath;
    private final String physicalTestResourcesPath;

    // - Terms
    // sourceFolder         : 소스 폴더                                      : src/main/java
    // sourcePath           : 소스 폴더 내의 Path                             : com/foo/bar
    // sourceFile           : 소스 폴더 내의 File(Path 포함)                   : com/foo/bar/SampleService.java
    // sourceFilePath       : 소스 폴더 내의 Path or File
    // fileName             : 파일명(Path 미포함)                             : SampleService.java
    // physicalJavaPath     : 프로젝트 자바소스 물리적 절대경로                    : C://Users/user/Documents/.../src/main/java
    // physicalSourcePath   : 소스가 되는 Path 의 물리적 절대경로                 : C://Users/user/Documents/.../src/main/java/com/foo/bar
    // physicalSourceFile   : 소스가 되는 File 의 물리적 절대경로                 : C://Users/user/Documents/.../src/main/java/com/foo/bar/SampleService.java
    // physicalSourceFilePath   : 소스가 되는 Path or File 의 물리적 절대경로

    public ProjectConfiguration(ConfigurationType type, String workspacePath, String projectName) {
        //
        this(type, workspacePath + PATH_DELIM + projectName);
    }

    public ProjectConfiguration(ConfigurationType type, String projectHomePath) {
        //
        this.type = type;
        this.projectHomePath = projectHomePath;
        this.physicalJavaPath = projectHomePath + PATH_DELIM + SRC_MAIN_JAVA;
        this.physicalResourcesPath = projectHomePath + PATH_DELIM + SRC_MAIN_RESOURCES;
        this.physicalTestPath = projectHomePath + PATH_DELIM + SRC_TEST_JAVA;
        this.physicalTestResourcesPath = projectHomePath + PATH_DELIM + SRC_TEST_RESOURCES;
    }

    // com/foo/bar/SampleService.java -> C://Users/user/Documents/.../src/main/java/com/foo/bar/SampleService.java
    public String makePhysicalJavaSourceFilePath(String sourceFilePath) {
        //
        return physicalJavaPath + PATH_DELIM + sourceFilePath;
    }

    // com/foo/bar/SampleSqlMap.xml -> C://Users/user/Documents/.../src/main/java/com/foo/bar/SampleSqlMap.xml
    public String makePhysicalResourceFilePath(String sourceFilePath) {
        //
        return physicalResourcesPath + PATH_DELIM + sourceFilePath;
    }

    // C://Users/user/Documents/.../src/main/java/com/foo/bar/SampleService.java -> com/foo/bar/SampleService.java
    public String extractSourceFilePath(String physicalSourceFilePath) {
        //
        int sourceFilePathIndex = computeSourceFilePathIndex(physicalSourceFilePath);

        if (sourceFilePathIndex < 0) {
            throw new RuntimeException("physicalSourceFilePath is not correct! : " + physicalSourceFilePath);
        }

        return physicalSourceFilePath.substring(sourceFilePathIndex);
    }

    private int computeSourceFilePathIndex(String physicalSourceFilePath) {
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

    public ConfigurationType getType() {
        return type;
    }

    public String getProjectHomePath() {
        return projectHomePath;
    }

    public String getPhysicalJavaPath() {
        return physicalJavaPath;
    }

    public String getPhysicalResourcesPath() {
        return physicalResourcesPath;
    }

    public String getPhysicalTestPath() {
        return physicalTestPath;
    }

    public String getPhysicalTestResourcesPath() {
        return physicalTestResourcesPath;
    }
}
