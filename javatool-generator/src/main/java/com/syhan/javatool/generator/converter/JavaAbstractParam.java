package com.syhan.javatool.generator.converter;

public class JavaAbstractParam {
    //
    private String targetFilePostfix;
    private String sourcePackage;
    private String sourceDtoPackage;

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
}
