package com.syhan.javatool.generator.model;

import java.util.ArrayList;
import java.util.List;

public class JavaModel {
    //
    private ClassType classType;
    private boolean isInterface;

    private List<MethodModel> methods;

    public JavaModel(String className, boolean isInterface) {
        //
        this.classType = new ClassType(className);
        this.isInterface = isInterface;
        this.methods = new ArrayList<>();
    }

    public JavaModel(String name, String packageName, boolean isInterface) {
        //
        this.classType = new ClassType(name, packageName);
        this.isInterface = isInterface;
        this.methods = new ArrayList<>();
    }

    public String getName() {
        //
        return classType.getName();
    }

    public String getPackageName() {
        //
        return classType.getPackageName();
    }

    public void addMethodModel(MethodModel methodModel) {
        //
        this.methods.add(methodModel);
    }

    public List<String> computeImports() {
        //
        List<String> usingClassNames = extractClassNames();
        return removeDuplicate(usingClassNames);
    }

    private List<String> removeDuplicate(List<String> nameList) {
        //
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < nameList.size(); i++) {
            if (!resultList.contains(nameList.get(i))) {
                resultList.add(nameList.get(i));
            }
        }
        return resultList;
    }

    private List<String> extractClassNames() {
        //
        List<String> classNames = new ArrayList<>();
        for (MethodModel methodModel : methods) {
            ClassType returnType = methodModel.getReturnType();
            if (returnType != null) {
                classNames.add(returnType.getClassName());
            }
            for (ClassType parameterType : methodModel.getParameterTypes()) {
                classNames.add(parameterType.getClassName());
            }
        }
        return classNames;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }

    public List<MethodModel> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodModel> methods) {
        this.methods = methods;
    }
}
