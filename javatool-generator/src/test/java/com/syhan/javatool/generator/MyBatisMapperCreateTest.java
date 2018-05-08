package com.syhan.javatool.generator;

import org.junit.Test;

import java.io.File;

public class MyBatisMapperCreateTest {
    //
    private static final String PROJECT_HOME = "../source-project";
    private static final String SOURCE_FILE = "foo/bar/SampleSqlMap.xml";
    private static final String SOURCE_FILE2 = "com/foo/bar/dao/mapper/Sample.xml";

    @Test
    public void testExecute() throws Exception {
        //
        MyBatisMapperCreate myBatisMapperCreate = new MyBatisMapperCreate();
        myBatisMapperCreate.execute(PROJECT_HOME, SOURCE_FILE);
    }

    @Test
    public void testExecute2() throws Exception {
        // Requirements
        // 1. return List type with Dao class.
        MyBatisMapperCreate myBatisMapperCreate = new MyBatisMapperCreate();
        myBatisMapperCreate.execute(PROJECT_HOME, SOURCE_FILE2);
    }

    static final String PATH_DELIM = File.separator;

    @Test
    public void testDaoPath() {
        //
        String xmlSourceFilePath = String.format("com%sfoo%sbar%sSample.xml", PATH_DELIM, PATH_DELIM, PATH_DELIM);

        String daoFilePath1 = toDaoFilePath(xmlSourceFilePath, 0);
        System.out.println("daoFilePath1:"+daoFilePath1);
        String daoFilePath2 = toDaoFilePath(xmlSourceFilePath, 1);
        System.out.println("daoFilePath2:"+daoFilePath2);
    }

    private String toDaoFilePath(String xmlSourceFilePath, int skipPackageCount) {
        // for Windows.
        String[] paths = xmlSourceFilePath.split(PATH_DELIM.equals("\\") ? "\\\\" : PATH_DELIM);
        int length = paths.length;

        String daoFilePath = "";
        for (int i = 0; i < length - 1 - skipPackageCount; i++) {
            daoFilePath += paths[i];
            daoFilePath += PATH_DELIM;
        }
        daoFilePath += toJava(paths[length - 1]);

        return daoFilePath;
    }

    private String toJava(String fileName) {
        return fileName.replaceAll("\\.xml", "Dao.java");
    }
}
