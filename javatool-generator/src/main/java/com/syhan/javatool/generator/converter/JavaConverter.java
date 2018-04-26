package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.share.config.ToolConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;

public class JavaConverter implements Converter {
    //
    private ToolConfiguration configuration;

    public JavaConverter(ToolConfiguration configuration) {
        //
        this.configuration = configuration;
    }

    @Override
    public void convert(String sourceFilePath) throws IOException {
        // sourceFile : com/foo/bar/SampleService.java
        System.out.println("sourceFilePath:"+sourceFilePath);
        String physicalSourceFilePath = configuration.getPhysicalSourceFilePath(sourceFilePath);
        System.out.println("physicalSourceFilePath:"+physicalSourceFilePath);
        JavaSource source = readSource(physicalSourceFilePath);

        String targetFilePath = sourceFilePath;
        System.out.println("targetFilePath:"+targetFilePath);
        String physicalTargetFilePath = configuration.getPhysicalTargetFilePath(targetFilePath);
        System.out.println("physicalTargetFilePath:"+physicalTargetFilePath);
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
