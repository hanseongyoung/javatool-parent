package com.syhan.javatool.generator.converter;

public class JavaAbstractParam {
    //
    private String targetFilePostfix;
    private String sourcePackage;
    private String sourceDtoPackage;
    private String newProjectName1;
    private String newProjectName2;

    public String getRemoteIdentifier() {
        //
        return newProjectName1 + "." + newProjectName2;
    }

    public String getTargetFilePostfix() {
        return targetFilePostfix;
    }

    public void setTargetFilePostfix(String targetFilePostfix) {
        this.targetFilePostfix = targetFilePostfix;
    }

    public String getSourcePackage() {
        return sourcePackage;
    }

    public void setSourcePackage(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

    public String getSourceDtoPackage() {
        return sourceDtoPackage;
    }

    public void setSourceDtoPackage(String sourceDtoPackage) {
        this.sourceDtoPackage = sourceDtoPackage;
    }

    public String getNewProjectName1() {
        return newProjectName1;
    }

    public void setNewProjectName1(String newProjectName1) {
        this.newProjectName1 = newProjectName1;
    }

    public String getNewProjectName2() {
        return newProjectName2;
    }

    public void setNewProjectName2(String newProjectName2) {
        this.newProjectName2 = newProjectName2;
    }
}
