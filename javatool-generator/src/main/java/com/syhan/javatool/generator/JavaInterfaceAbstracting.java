package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.JavaInterfaceAbstracter;
import com.syhan.javatool.share.args.OptionParser;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

import java.io.IOException;

public class JavaInterfaceAbstracting {
    //
    public void execute(String sourceProjectHome, String targetProjectHome, String sourceBasePackage, String sourceFileName) throws IOException {
        //
        ProjectConfiguration sourceConfiguration = new ProjectConfiguration(ConfigurationType.Source, sourceProjectHome);
        ProjectConfiguration targetConfiguration = new ProjectConfiguration(ConfigurationType.Target, targetProjectHome);

        JavaInterfaceAbstracter abstracter = new JavaInterfaceAbstracter(sourceConfiguration, targetConfiguration, targetConfiguration, null, sourceBasePackage);
        abstracter.convert(sourceFileName);
    }

    public static void main(String[] args) throws IOException {
        //
        OptionParser parser = new OptionParser();
        parser.accepts("sourceProjectHome", "A source project home path")
                .accepts("targetProjectHome", "A target project home path")
                .accepts("sourceBasePackage", "Source Base Package ex) com.foo")
                .accepts("sourceFileName", "A java class File name in source folder ex) com/foo/bar/SampleService.java")
                .parse(args);

        JavaInterfaceAbstracting javaInterfaceAbstracting = new JavaInterfaceAbstracting();
        javaInterfaceAbstracting.execute(parser.get("sourceProjectHome"), parser.get("targetProjectHome"), parser.get("sourceBasePackage"), parser.get("sourceFileName"));
    }
}
