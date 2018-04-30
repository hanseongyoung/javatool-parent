package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.JavaConverter;
import com.syhan.javatool.generator.converter.PackageConverter;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

// 규칙
// com.foo.bar.service.SampleService --> com.foo.bar.logic.SampleLogic : **/service/
public class PackageJavaConvert {
    //
    private static final String SOURCE_PROJECT_PATH = "./source-project";
    private static final String TARGET_PROJECT_PATH = "./target-project";

    public static void main(String[] args) throws Exception {
        //
        ProjectConfiguration sourceConfiguration = new ProjectConfiguration(ConfigurationType.Source, SOURCE_PROJECT_PATH);
        ProjectConfiguration targetConfiguration = new ProjectConfiguration(ConfigurationType.Target, TARGET_PROJECT_PATH);

        // package convert
        JavaConverter javaConverter = new JavaConverter(sourceConfiguration, targetConfiguration);
        PackageConverter converter = new PackageConverter(javaConverter);
        converter.convert("com.foo");
    }
}
