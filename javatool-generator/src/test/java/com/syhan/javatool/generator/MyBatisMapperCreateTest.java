package com.syhan.javatool.generator;

import org.junit.Test;

public class MyBatisMapperCreateTest {
    //
    private static final String PROJECT_HOME = "../source-project";
    private static final String SOURCE_FILE = "foo/bar/SampleSqlMap.xml";

    @Test
    public void testExecute() throws Exception {
        //
        MyBatisMapperCreate myBatisMapperCreate = new MyBatisMapperCreate();
        myBatisMapperCreate.execute(PROJECT_HOME, SOURCE_FILE);
    }
}
