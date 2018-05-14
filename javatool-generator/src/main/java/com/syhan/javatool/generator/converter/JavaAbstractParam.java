package com.syhan.javatool.generator.converter;

public class JavaAbstractParam {
    //
    private String sourceBasePackage;
    private String implNameFrom;
    private String implNameTo;

    public JavaAbstractParam(String sourceBasePackage, String implNameFrom, String implNameTo) {
        //
        this.sourceBasePackage = sourceBasePackage;
        this.implNameFrom = implNameFrom;
        this.implNameTo = implNameTo;
    }

    public String getSourceBasePackage() {
        return sourceBasePackage;
    }

    public void setSourceBasePackage(String sourceBasePackage) {
        this.sourceBasePackage = sourceBasePackage;
    }

    public String getImplNameFrom() {
        return implNameFrom;
    }

    public void setImplNameFrom(String implNameFrom) {
        this.implNameFrom = implNameFrom;
    }

    public String getImplNameTo() {
        return implNameTo;
    }

    public void setImplNameTo(String implNameTo) {
        this.implNameTo = implNameTo;
    }
}
