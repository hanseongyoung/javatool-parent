package com.syhan.javatool.share.config;

import java.io.File;

public class SourceFolders {
    //
    public final String SRC_MAIN_JAVA;
    public final String SRC_MAIN_RESOURCES;
    public final String SRC_TEST_JAVA;
    public final String SRC_TEST_RESOURCES;

    private SourceFolders() {
        //
        String PATH_DELIM = File.separator;
        this.SRC_MAIN_JAVA      = String.format("src%smain%sjava", PATH_DELIM, PATH_DELIM);
        this.SRC_MAIN_RESOURCES = String.format("src%smain%sresources", PATH_DELIM, PATH_DELIM);
        this.SRC_TEST_JAVA      = String.format("src%stest%sjava", PATH_DELIM, PATH_DELIM);
        this.SRC_TEST_RESOURCES = String.format("src%stest%sresources", PATH_DELIM, PATH_DELIM);
    }

    private SourceFolders(String srcMainJava, String srcMainResources) {
        //
        String PATH_DELIM = File.separator;
        this.SRC_MAIN_JAVA = srcMainJava == null ? String.format("src%smain%sjava", PATH_DELIM, PATH_DELIM) : srcMainJava;
        this.SRC_MAIN_RESOURCES = srcMainResources == null ? String.format("src%smain%sresources", PATH_DELIM, PATH_DELIM) : srcMainResources;
        this.SRC_TEST_JAVA      = String.format("src%stest%sjava", PATH_DELIM, PATH_DELIM);
        this.SRC_TEST_RESOURCES = String.format("src%stest%sresources", PATH_DELIM, PATH_DELIM);
    }

    public static SourceFolders getDefault() {
        //
        return new SourceFolders();
    }

    public static SourceFolders newSourceFolders(String srcMainJava, String srcMainResources) {
        //
        return new SourceFolders(srcMainJava, srcMainResources);
    }
}
