package com.syhan.javatool.generator.writer;

import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.share.config.ProjectConfiguration;

import java.io.IOException;

public class JavaWriter implements Writer {
    //
    private ProjectConfiguration configuration;

    public JavaWriter(ProjectConfiguration configuration) {
        //
        this.configuration = configuration;
    }

    @Override
    public void write(JavaSource source) throws IOException {
        //
        String targetFilePath = source.getSourceFilePath();
        String physicalTargetFilePath = configuration.makePhysicalJavaSourceFilePath(targetFilePath);
        writeSource(source, physicalTargetFilePath);
    }

    private void writeSource(JavaSource source, String physicalTargetFilePath) throws IOException {
        //
        source.write(physicalTargetFilePath);
    }
}
