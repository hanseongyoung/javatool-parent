package com.syhan.javatool.generator.model;

import java.util.ArrayList;
import java.util.List;

public class JavaModel {
    //
    private ClassType classType;
    private AnnotationType annotation;
    private boolean isInterface;

    private List<MethodModel> methods;

    public JavaModel(String className, boolean isInterface) {
        //
        this.classType = ClassType.newClassType(className);
        this.isInterface = isInterface;
        this.methods = new ArrayList<>();
    }

    public JavaModel(String name, String packageName, boolean isInterface) {
        //
        this.classType = ClassType.newClassType(name, packageName);
        this.isInterface = isInterface;
        this.methods = new ArrayList<>();
    }

    public MethodModel findMethodByName(String methodName) {
        if (methods == null || methods.isEmpty()) {
            return null;
        }

        for (MethodModel methodModel : methods) {
            if(methodModel.getName().equals(methodName)) {
                return methodModel;
            }
        }
        return null;
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

    public boolean hasAnnotation() {
        //
        return this.annotation != null;
    }

    public List<String> computeImports() {
        //
        List<String> usingClassNames = extractUsingClassNames();
        usingClassNames = removeDuplicate(usingClassNames);
        return removePrimitiveType(usingClassNames);
    }

    public List<String> computeMethodUsingClasses() {
        //
        List<String> usingClassNames = extractMethodUsingClassNames();
        usingClassNames = removeDuplicate(usingClassNames);
        return removePrimitiveType(usingClassNames);
    }

    private List<String> removePrimitiveType(List<String> nameList) {
        //
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < nameList.size(); i++) {
            String name = nameList.get(i);
            if (name.toUpperCase().equals("INT")
                    || name.toUpperCase().equals("LONG")
                    || name.toUpperCase().equals("CHAR")
                    || name.toUpperCase().equals("BOOLEAN")
                    || name.toUpperCase().equals("STRING")) {
                //
            } else {
                resultList.add(name);
            }
        }
        return resultList;
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

    private List<String> extractUsingClassNames() {
        //
        List<String> classNames = new ArrayList<>();

        if (annotation != null) {
            classNames.add(annotation.getClassName());
        }

        classNames.addAll(extractMethodUsingClassNames());
        return classNames;
    }

    private List<String> extractMethodUsingClassNames() {
        //
        List<String> classNames = new ArrayList<>();

        for (MethodModel methodModel : methods) {
            ClassType returnType = methodModel.getReturnType();
            if (returnType != null && !returnType.isPrimitive()) {
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

    public AnnotationType getAnnotation() {
        return annotation;
    }

    public void setAnnotation(AnnotationType annotation) {
        this.annotation = annotation;
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
