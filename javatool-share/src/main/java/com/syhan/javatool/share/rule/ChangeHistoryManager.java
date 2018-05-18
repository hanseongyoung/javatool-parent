package com.syhan.javatool.share.rule;

import com.syhan.javatool.share.util.file.PathUtil;

import java.util.HashMap;
import java.util.Map;

public class ChangeHistoryManager {
    //
    public static ChangeHistoryManager CHANGE_HISTORY = new ChangeHistoryManager();

    // key --> className
    private Map<String, PackageRule.ChangeImport> classNameChange = new HashMap<>();

    private ChangeHistoryManager(){
        //
    }

    public boolean containsKeyBySourceFileName(String sourceFileName) {
        //
        String className = PathUtil.toClassName(sourceFileName);
        return classNameChange.containsKey(className);
    }

    public void put(PackageRule.ChangeImport changeImport) {
        //
        this.classNameChange.put(changeImport.before, changeImport);
    }

    public String findChangeClassName(String className) {
        //
        PackageRule.ChangeImport changeImport = classNameChange.get(className);
        if (changeImport == null) {
            return null;
        }
        return changeImport.after;
    }
}
