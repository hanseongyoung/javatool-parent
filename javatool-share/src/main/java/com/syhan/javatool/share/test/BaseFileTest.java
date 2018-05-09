package com.syhan.javatool.share.test;

import org.apache.commons.io.FileUtils;
import org.junit.Before;

import java.io.File;

public class BaseFileTest {
    //
    protected String testDirName = "./target/tested-files";

    protected BaseFileTest() {
        //
    }

    @Before
    public void initFileEnv() throws Exception {
        //
        File testBundleDir = FileUtils.getFile(testDirName);
        if (testBundleDir.exists()) {
            FileUtils.forceDelete(testBundleDir);
        }
        FileUtils.forceMkdir(testBundleDir);
    }

    /*
    @After
    public void destoryFileEnv() throws Exception {
        //
        File testBundleDir = FileUtils.getFile(testDirName);
        if (testBundleDir.exists()) {
            FileUtils.forceDelete(testBundleDir);
        }
    }
    */
}
