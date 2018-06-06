package com.syhan.javatool.generator.model;

public class ParameterModel {
    //
    private ClassType type;
    private String varName;

    public ParameterModel(ClassType type, String varName) {
        //
        this.type = type;
        this.varName = varName;
    }

    public ParameterModel(ParameterModel other) {
        //
        this.type = ClassType.copyOf(other.type);
        this.varName = other.varName;
    }

    public ClassType getType() {
        return type;
    }

    public void setType(ClassType type) {
        this.type = type;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }
}
