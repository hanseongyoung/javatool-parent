package com.syhan.javatool.generator;

import com.syhan.javatool.generator.converter.MyBatisMapperCreator;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

public class XmlRead {
    //
    private static final String SOURCE_PROJECT_PATH = "/Users/daniel/Documents/work/source_gen/javatool-parent/source-project";

    public static void main(String[] args) throws Exception {
        //
        ProjectConfiguration sourceConfiguration = new ProjectConfiguration(ConfigurationType.Source, SOURCE_PROJECT_PATH);
        ProjectConfiguration targetConfiguration = new ProjectConfiguration(ConfigurationType.Target, SOURCE_PROJECT_PATH);

        MyBatisMapperCreator myBatisMapperCreator = new MyBatisMapperCreator(sourceConfiguration, targetConfiguration);
        myBatisMapperCreator.convert("foo/bar/SampleSqlMap.xml");
    }

}
