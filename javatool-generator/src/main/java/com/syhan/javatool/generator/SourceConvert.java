package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.Converter;
import com.syhan.javatool.generator.converter.JavaConverter;
import com.syhan.javatool.share.config.ToolConfiguration;

// 규칙
// com.foo.bar.service.SampleService --> com.foo.bar.logic.SampleLogic : **/service/
public class SourceConvert {
    //
    private static final String SOURCE_PATH = "/Users/daniel/Documents/work/source_gen/source-gen-work/source-project";
    private static final String TARGET_PATH = "/Users/daniel/Documents/work/source_gen/source-gen-work/target-project";

    public static void main(String[] args) throws Exception {
        //
        ToolConfiguration configuration = new ToolConfiguration(SOURCE_PATH, TARGET_PATH);
        // 1. java file convert
        Converter converter = new JavaConverter(configuration);
        converter.convert("com/foo/bar/service/SampleService.java");

        // 2. package convert
        //Converter converter = new PackageConverter(configuration);
        //converter.convert("com.foo");
    }
}
