package com.syhan.javatool.share.util.file;

import com.syhan.javatool.share.config.ProjectSources;
import com.syhan.javatool.share.data.Pair;
import com.syhan.javatool.share.util.string.StringUtil;

public abstract class PathUtil {
    //
    // mc.oo.od.Sample -> mc.oo.od, Sample
    public static Pair<String, String> devideClassName(String className) {
        //
        String[] packageFrags = className.split("\\.");
        int length = packageFrags.length;

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length - 1; i++) {
            sb.append(packageFrags[i]);
            if (i < length - 2) {
                sb.append(".");
            }
        }

        return new Pair<>(sb.toString(), packageFrags[length - 1]);
    }


    // com.foo.bar.SampleDto -> com/foo/bar/SampleDto.java
    public static String toSourceFileName(String className, String extension) {
        //
        String[] packageFrags = className.split("\\.");
        int length = packageFrags.length;

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; i++) {
            sb.append(packageFrags[i]);
            if (i < length - 1) {
                sb.append(ProjectSources.PATH_DELIM);
            }
        }

        sb.append(".");

        // add extension
        if (StringUtil.isNotEmpty(extension)) {
            sb.append(extension);
        }
        return sb.toString();
    }

    // SampleService -> SampleLogic
    public static String changeName(String name, String skipPostFix, String addPostFix) {
        //
        StringBuffer sb = new StringBuffer();
        if (skipPostFix != null && name.endsWith(skipPostFix)) {
            String sub = name.substring(0, name.length() - skipPostFix.length());
            sb.append(sub);
        } else {
            sb.append(name);
        }

        if (addPostFix != null) {
            sb.append(addPostFix);
        }

        return sb.toString();
    }

    // com.foo.bar --> com.foo.spec
    public static String changePackage(String packageName, int skipPackageCount, String[] addPackages) {
        //
        String[] packageFrags = packageName.split("\\.");
        int length = packageFrags.length;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length - skipPackageCount; i++) {
            sb.append(packageFrags[i]);
            sb.append(".");
        }

        if (addPackages != null && addPackages.length > 0) {
            for (int i = 0; i < addPackages.length; i++) {
                sb.append(addPackages[i]);
                sb.append(".");
            }
        }

        // remove last '.'
        String result = sb.toString();
        if (result.endsWith(".")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    // com/foo/bar/Sample.xml --> com/foo/bar/SampleDao.java
    public static String changeFileName(String sourceFilePath, String namePostFix, String extension) {
        //
        return changePath(sourceFilePath, 0, null, namePostFix, extension);
    }

    // [folder with skipFolder]/[addFolders]/[name][namePostFix].[extension]
    public static String changePath(String sourceFilePath, int skipFolderCount, String[] addFolders, String namePostFix, String extension) {
        //
        String[] paths = sourceFilePath.split(ProjectSources.PATH_DELIM.equals("\\") ? "\\\\" : ProjectSources.PATH_DELIM); // for Windows
        int length = paths.length;
        String fileName = paths[length - 1];
        int dotIndex = fileName.indexOf(".");
        String name = fileName.substring(0, dotIndex);
        String originExtension = fileName.substring(dotIndex + 1);

        // make new path with skipCount
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length - 1 - skipFolderCount; i++) {
            sb.append(paths[i]);
            sb.append(ProjectSources.PATH_DELIM);
        }

        // add folders to new path
        if (addFolders != null && addFolders.length > 0) {
            for (int i = 0; i < addFolders.length; i++) {
                sb.append(addFolders[i]);
                sb.append(ProjectSources.PATH_DELIM);
            }
        }

        // namePostFix
        sb.append(name);
        if (StringUtil.isNotEmpty(namePostFix)) {
            sb.append(namePostFix);
        }
        sb.append(".");

        // add extension
        if (StringUtil.isNotEmpty(extension)) {
            sb.append(extension);
        } else {
            sb.append(originExtension);
        }

        return sb.toString();
    }
}
