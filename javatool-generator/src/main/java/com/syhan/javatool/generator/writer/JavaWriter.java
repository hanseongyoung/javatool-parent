package com.syhan.javatool.generator.writer;

import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.share.config.ToolConfiguration;

import java.io.IOException;

public class JavaWriter implements Writer {
    //
    private ToolConfiguration configuration;

    public JavaWriter(ToolConfiguration configuration) {
        //
        this.configuration = configuration;
    }

    @Override
    public void write(JavaSource source) throws IOException {
        //
        String targetFilePath = source.getSourceFilePath();
        String physicalTargetFilePath = configuration.getPhysicalTargetFilePath(targetFilePath);
        writeSource(source, physicalTargetFilePath);
    }

    private void writeSource(JavaSource source, String physicalTargetFilePath) throws IOException {
        //
        source.write(physicalTargetFilePath);
    }
}
