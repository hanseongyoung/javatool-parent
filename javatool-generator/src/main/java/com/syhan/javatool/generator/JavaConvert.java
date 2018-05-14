package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.Converter;
import com.syhan.javatool.generator.converter.JavaConverter;
import com.syhan.javatool.share.args.OptionParser;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.rule.PackageRule;

import java.io.IOException;

public class JavaConvert {
    //
    /**
     * execute JavaConverter
     * @param sourceProjectHome ex) ./source-project
     * @param targetProjectHome ex) ./target-project
     * @param sourceFileName    ex) com/foo/bar/service/SampleService.java
     * @throws IOException
     */
    public void execute(String sourceProjectHome, String targetProjectHome, String sourceFileName) throws IOException {
        //
        ProjectConfiguration sourceConfiguration = new ProjectConfiguration(ConfigurationType.Source, sourceProjectHome);
        ProjectConfiguration targetConfiguration = new ProjectConfiguration(ConfigurationType.Target, targetProjectHome);

        // java file convert
        // FIXME : remove hardcode
        PackageRule packageRule = PackageRule.newInstance()
                .add(0, "com", "kr.co")
                .add(2, "bar", "b")
                .add(3, "controller", "rest")
                .add(3, "service", "logic");
        Converter converter = new JavaConverter(sourceConfiguration, targetConfiguration, packageRule);
        converter.convert(sourceFileName);
    }

    public static void main(String[] args) throws Exception {
        //
        OptionParser parser = new OptionParser();
        parser.accepts("sourceProjectHome", "A source project home path")
                .accepts("targetProjectHome", "A target project home path")
                .accepts("sourceFileName", "A java class File name in source folder ex) com/foo/bar/SampleService.java")
                .parse(args);

        JavaConvert javaConvert = new JavaConvert();
        javaConvert.execute(parser.get("sourceProjectHome"), parser.get("targetProjectHome"), parser.get("sourceFileName"));
    }
}
