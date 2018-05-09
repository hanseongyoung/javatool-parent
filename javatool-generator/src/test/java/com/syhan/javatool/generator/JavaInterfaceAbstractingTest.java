package com.syhan.javatool.generator;

import com.syhan.javatool.share.test.BaseFileTest;
import org.junit.Test;

import java.io.IOException;

public class JavaInterfaceAbstractingTest extends BaseFileTest {
    //
    private static final String SOURCE_PROJECT_HOME = "../source-project";
    private static final String SOURCE_FILE_NAME = "com/foo/bar/service/SampleService.java";

    @Test
    public void testExecute() throws IOException {
        //
        JavaInterfaceAbstracting abstracting = new JavaInterfaceAbstracting();
        abstracting.execute(SOURCE_PROJECT_HOME, super.testDirName, SOURCE_FILE_NAME);
    }
}
