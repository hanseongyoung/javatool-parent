package com.syhan.javatool.generator.model;

import java.util.ArrayList;
import java.util.List;

public class MethodModel {
    //
    private String name;
    private String access;
    private ClassType returnType;
    private List<ClassType> parameterTypes;

    public MethodModel(String name, ClassType returnType) {
        //
        this.name = name;
        this.returnType = returnType;
        this.parameterTypes = new ArrayList<>();
    }

    public void addParameterType(ClassType classType) {
        //
        this.parameterTypes.add(classType);
    }

    public boolean isVoid() {
        return returnType == null;
    }

    public boolean isPrimitive() {
        //
        if (returnType == null) return false;
        return returnType.isPrimitive();
    }

    public boolean isPublic() {
        //
        return "public".equals(access);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassType getReturnType() {
        return returnType;
    }

    public void setReturnType(ClassType returnType) {
        this.returnType = returnType;
    }

    public List<ClassType> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<ClassType> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }
}
