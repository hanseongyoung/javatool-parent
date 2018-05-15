package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.MyBatisMapperCreator;
import com.syhan.javatool.share.args.OptionParser;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

import java.io.IOException;

public class MyBatisMapperCreate {
    //
    public void execute(String projectHome, String sourceFile) throws IOException {
        //
        ProjectConfiguration sourceConfiguration = new ProjectConfiguration(ConfigurationType.Source, projectHome);
        ProjectConfiguration targetConfiguration = new ProjectConfiguration(ConfigurationType.Target, projectHome);

        MyBatisMapperCreator myBatisMapperCreator = new MyBatisMapperCreator(sourceConfiguration, sourceConfiguration,
                targetConfiguration);
        myBatisMapperCreator.convert(sourceFile);
    }


    public static void main(String[] args) throws Exception {
        //
        OptionParser parser = new OptionParser();
        parser.accepts("projectHome", "The project home path")
                .accepts("sourceFile", "SqlMap file")
                .parse(args);

        MyBatisMapperCreate myBatisMapperCreate = new MyBatisMapperCreate();
        System.out.println(parser);
        myBatisMapperCreate.execute(parser.get("projectHome"), parser.get("sourceFile"));

    }

}
