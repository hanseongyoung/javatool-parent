package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.JavaConverter;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.test.BaseFileTest;
import org.junit.Test;

import java.io.IOException;

public class JavaConverterTest extends BaseFileTest {
    //
    private static final String SOURCE_PROJECT_HOME = "../source-project";
    private static final String SOURCE_FILE_NAME = "com/foo/bar/service/SampleService.java";

    @Test
    public void testConvert() throws IOException {
        //
        ProjectConfiguration sourceConfig = new ProjectConfiguration(ConfigurationType.Source, SOURCE_PROJECT_HOME);
        ProjectConfiguration targetConfig = new ProjectConfiguration(ConfigurationType.Target, super.testDirName);
        PackageRule packageRule = PackageRule.newInstance()
                .add(0, "com", "kr");

        JavaConverter javaConverter = new JavaConverter(sourceConfig, targetConfig, null, packageRule);
        javaConverter.convert(SOURCE_FILE_NAME);
    }
}
