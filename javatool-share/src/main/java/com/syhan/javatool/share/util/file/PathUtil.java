package com.syhan.javatool.share.util.file;

import com.syhan.javatool.share.config.ProjectSources;
import com.syhan.javatool.share.util.string.StringUtil;

public abstract class PathUtil {
    //
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
