package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.Converter;
import com.syhan.javatool.generator.converter.PackageConverter;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

// 규칙
// com.foo.bar.service.SampleService --> com.foo.bar.logic.SampleLogic : **/service/
public class SourceConvert {
    //
    private static final String SOURCE_PROJECT_PATH = "/Users/daniel/Documents/work/source_gen/source-gen-work/source-project";
    private static final String TARGET_PROJECT_PATH = "/Users/daniel/Documents/work/source_gen/source-gen-work/target-project";

    public static void main(String[] args) throws Exception {
        //
        ProjectConfiguration sourceConfiguration = new ProjectConfiguration(ConfigurationType.Source, SOURCE_PROJECT_PATH);
        ProjectConfiguration targetConfiguration = new ProjectConfiguration(ConfigurationType.Target, TARGET_PROJECT_PATH);

        // 1. java file convert
        //Converter converter = new JavaConverter(sourceConfiguration, targetConfiguration);
        //converter.convert("com/foo/bar/service/SampleService.java");

        // 2. package convert
        Converter converter = new PackageConverter(sourceConfiguration, targetConfiguration);
        converter.convert("com.foo");
    }
}
