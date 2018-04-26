package com.syhan.javatool.share.util;

import com.syhan.javatool.share.test.BaseFileTest;
import org.junit.Test;

import java.io.File;

public class FolderUtilTest extends BaseFileTest {
    //
    @Test
    public void testCreate() {
        //
        String physicalPath = testDirName + File.separator + "foo" + File.separator + "bar";
        FolderUtil.mkdir(physicalPath);
    }
}
