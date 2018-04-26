package com.syhan.javatool.generator.writer;

import com.syhan.javatool.generator.source.JavaSource;

import java.io.IOException;

public interface Writer {
    //
    void write(JavaSource source) throws IOException;
}
