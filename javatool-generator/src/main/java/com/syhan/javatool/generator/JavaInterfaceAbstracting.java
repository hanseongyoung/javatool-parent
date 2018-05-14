package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.JavaInterfaceAbstracter;
import com.syhan.javatool.share.args.OptionParser;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

import java.io.IOException;

public class JavaInterfaceAbstracting {
    //
    public void execute(String sourceProjectHome, String targetProjectHome, String sourceFileName) throws IOException {
        //
        ProjectConfiguration sourceConfiguration = new ProjectConfiguration(ConfigurationType.Source, sourceProjectHome);
        ProjectConfiguration targetConfiguration = new ProjectConfiguration(ConfigurationType.Target, targetProjectHome);

        JavaInterfaceAbstracter abstracter = new JavaInterfaceAbstracter(sourceConfiguration, targetConfiguration, targetConfiguration, null);
        abstracter.convert(sourceFileName);
    }

    public static void main(String[] args) throws IOException {
        //
        OptionParser parser = new OptionParser();
        parser.accepts("sourceProjectHome", "A source project home path")
                .accepts("targetProjectHome", "A target project home path")
                .accepts("sourceFileName", "A java class File name in source folder ex) com/foo/bar/SampleService.java")
                .parse(args);

        JavaInterfaceAbstracting javaInterfaceAbstracting = new JavaInterfaceAbstracting();
        javaInterfaceAbstracting.execute(parser.get("sourceProjectHome"), parser.get("targetProjectHome"), parser.get("sourceFileName"));
    }
}
