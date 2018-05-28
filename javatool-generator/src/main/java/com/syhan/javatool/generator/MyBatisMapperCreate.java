package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.MyBatisMapperCreator;
import com.syhan.javatool.share.args.OptionParser;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MyBatisMapperCreate {
    //
    private static final Logger logger = LoggerFactory.getLogger(MyBatisMapperCreate.class);

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
        logger.info(parser.toString());
        myBatisMapperCreate.execute(parser.get("projectHome"), parser.get("sourceFile"));

    }

}
