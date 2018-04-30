package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.Converter;
import com.syhan.javatool.generator.converter.JavaConverter;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

// 규칙
// com.foo.bar.service.SampleService --> com.foo.bar.logic.SampleLogic : **/service/
public class JavaConvert {
    //
    private static final String SOURCE_PROJECT_PATH = "./source-project";
    private static final String TARGET_PROJECT_PATH = "./target-project";

    public static void main(String[] args) throws Exception {
        //
        ProjectConfiguration sourceConfiguration = new ProjectConfiguration(ConfigurationType.Source, SOURCE_PROJECT_PATH);
        ProjectConfiguration targetConfiguration = new ProjectConfiguration(ConfigurationType.Target, TARGET_PROJECT_PATH);

        // java file convert
        Converter converter = new JavaConverter(sourceConfiguration, targetConfiguration);
        converter.convert("com/foo/bar/service/SampleService.java");
    }
}
