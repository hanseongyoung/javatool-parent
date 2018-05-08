package com.syhan.javatool.generator.reader;

import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.share.config.ProjectConfiguration;

import java.io.IOException;

public class JavaReader implements Reader<JavaSource> {
    //
    private ProjectConfiguration configuration;

    public JavaReader(ProjectConfiguration configuration) {
        //
        this.configuration = configuration;
    }

    @Override
    public JavaSource read(String sourceFilePath) throws IOException {
        //
        String physicalSourceFilePath = configuration.makePhysicalJavaSourceFilePath(sourceFilePath);
        return new JavaSource(physicalSourceFilePath);
    }

    public boolean exists(String sourceFilePath) {
        //
        String physicalSourceFilePath = configuration.makePhysicalJavaSourceFilePath(sourceFilePath);
        return JavaSource.exists(physicalSourceFilePath);
    }
}
