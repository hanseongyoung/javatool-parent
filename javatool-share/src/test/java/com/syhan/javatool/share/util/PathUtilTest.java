package com.syhan.javatool.share.util;

import com.syhan.javatool.share.util.file.PathUtil;
import org.junit.Assert;
import org.junit.Test;

public class PathUtilTest {
    //
    @Test
    public void testPath1() {
        //
        String before = "com/foo/bar/Sample.xml";
        String after  = "com/foo/spec/SampleDao.java";

        String result = PathUtil.changePath(before, 1, new String[]{"spec"}, "Dao", "java");
        System.out.println(result);
        Assert.assertEquals(after, result);
    }

    @Test
    public void testPath2() {
        //
        String before = "com/foo/bar/Sample.xml";
        String after  = "com/foo/bar/SampleDao.java";

        String result = PathUtil.changeFileName(before, "Dao", "java");
        System.out.println(result);
        Assert.assertEquals(after, result);
    }

    // com/foo/bar/Sample.xml -> com/foo/bar/SampleDao.java or com/foo/SampleDao.java
    @Test
    public void testPath3() {
        //
        String before = "com/foo/bar/Sample.xml";
        String after  = "com/foo/SampleDao.java";

        String result = PathUtil.changePath(before, 1, null, "Dao", "java");
        System.out.println(result);
        Assert.assertEquals(after, result);
    }
}
