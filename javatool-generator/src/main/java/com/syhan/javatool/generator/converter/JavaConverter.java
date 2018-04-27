package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.share.config.ProjectConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;

public class JavaConverter implements Converter {
    //
    private ProjectConfiguration sourceConfiguration;
    private ProjectConfiguration targetConfiguration;

    public JavaConverter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration) {
        //
        this.sourceConfiguration = sourceConfiguration;
        this.targetConfiguration = targetConfiguration;
    }

    @Override
    public void convert(String sourceFilePath) throws IOException {
        // sourceFile : com/foo/bar/SampleService.java
        String physicalSourceFilePath = sourceConfiguration.makePhysicalJavaSourceFilePath(sourceFilePath);
        JavaSource source = readSource(physicalSourceFilePath);

        String targetFilePath = sourceFilePath;
        String physicalTargetFilePath = targetConfiguration.makePhysicalJavaSourceFilePath(targetFilePath);
        writeSource(source, physicalTargetFilePath);
    }

    private JavaSource readSource(String physicalSourceFilePath) throws FileNotFoundException {
        //
        return new JavaSource(physicalSourceFilePath);
    }

    private void writeSource(JavaSource source, String physicalTargetFilePath) throws IOException {
        //
        source.write(physicalTargetFilePath);
    }
}
