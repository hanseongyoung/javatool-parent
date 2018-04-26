package com.syhan.javatool.share.config;

import java.io.File;

public class ToolConfiguration {
    //
    private static final String PATH_DELIM = File.separator;
    private final String sourceJavaFolder = String.format("src%smain%sjava", PATH_DELIM, PATH_DELIM);    // src/main/java
    private final String targetJavaFolder = String.format("src%smain%sjava", PATH_DELIM, PATH_DELIM);    // src/main/java
    private final String physicalSourcePrefix;
    private final String physicalTargetPrefix;

    // - Terms
    // sourceFolder         : 소스 폴더                                      : src/main/java
    // sourcePath           : 소스 폴더 내의 Path                             : com/foo/bar
    // sourceFile           : 소스 폴더 내의 File(Path 포함)                   : com/foo/bar/SampleService.java
    // sourceFilePath       : 소스 폴더 내의 Path or File
    // fileName             : 파일명(Path 미포함)                             : SampleService.java
    // physicalSourcePath   : 소스가 되는 Path 의 물리적 절대경로                 : C://Users/user/Documents/.../src/main/java/com/foo/bar
    // physicalSourceFile   : 소스가 되는 File 의 물리적 절대경로                 : C://Users/user/Documents/.../src/main/java/com/foo/bar/SampleService.java
    // physicalSourceFilePath   : 소스가 되는 Path or File 의 물리적 절대경로

    public ToolConfiguration(String sourceHomePath, String targetHomePath) {
        //
        this.physicalSourcePrefix = sourceHomePath + PATH_DELIM + sourceJavaFolder + PATH_DELIM;
        this.physicalTargetPrefix = targetHomePath + PATH_DELIM + targetJavaFolder + PATH_DELIM;
    }

    public ToolConfiguration(String sourceHomePath) {
        //
        this.physicalSourcePrefix = sourceHomePath + PATH_DELIM + sourceJavaFolder + PATH_DELIM;
        this.physicalTargetPrefix = sourceHomePath + PATH_DELIM + sourceJavaFolder + PATH_DELIM;
    }

    public String getPhysicalTargetFilePath(String targetFilePath) {
        //
        return physicalTargetPrefix + targetFilePath;
    }

    public String getPhysicalSourceFilePath(String sourceFilePath) {
        //
        return physicalSourcePrefix + sourceFilePath;
    }

    public String getSourceFilePath(String physicalSourceFilePath) {
        //
        if (!physicalSourceFilePath.startsWith(physicalSourcePrefix)) {
            throw new RuntimeException("physicalSourceFilePath is not correct! : " + physicalSourceFilePath);
        }

        return physicalSourceFilePath.substring(physicalSourcePrefix.length());
    }

}
