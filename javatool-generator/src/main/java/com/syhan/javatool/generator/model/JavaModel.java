package com.syhan.javatool.generator.model;

import com.github.javaparser.ast.comments.Comment;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.json.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class JavaModel {
    //
    private ClassType classType;
    private AnnotationType annotation;
    private boolean isInterface;

    private List<MethodModel> methods;

    private Comment nodeComment;
    private Comment typeComment;

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

    public JavaModel(JavaModel other) {
        //
        this.classType = ClassType.copyOf(other.classType);
        this.annotation = AnnotationType.copyOf(other.annotation);
        this.isInterface = other.isInterface;
        this.methods = new ArrayList<>();
        for (MethodModel method : other.methods) {
            this.methods.add(new MethodModel(method));
        }
        this.nodeComment = other.nodeComment;
        this.typeComment = other.typeComment;
    }

    public void changeName(NameRule nameRule) {
        //
        if (nameRule == null) {
            return;
        }
        classType.changeName(nameRule);
    }

    public void changePackage(PackageRule packageRule) {
        //
        if (packageRule == null) {
            return;
        }

        classType.changePackage(packageRule);
    }

    public void changeMethodUsingClassPackageName(NameRule nameRule, PackageRule packageRule) {
        //
        if (packageRule == null && nameRule == null) {
            return;
        }

        for (MethodModel methodModel : methods) {
            ClassType returnType = methodModel.getReturnType();
            changeClassTypeName(returnType, nameRule, packageRule);
            changeTypeArgumentName(returnType, nameRule, packageRule);

            for (ParameterModel parameterModel : methodModel.getParameterModels()) {
                ClassType parameterType = parameterModel.getType();
                changeClassTypeName(parameterType, nameRule, packageRule);
                changeTypeArgumentName(parameterType, nameRule, packageRule);
            }
        }
    }

    private void changeTypeArgumentName(ClassType classType, NameRule nameRule, PackageRule packageRule) {
        //
        if (classType == null) {
            return;
        }
        if (!classType.hasTypeArgument()) {
            return;
        }

        for (ClassType typeArgument : classType.getTypeArguments()) {
            changeClassTypeName(typeArgument, nameRule, packageRule);
        }
    }

    private void changeClassTypeName(ClassType classType, NameRule nameRule, PackageRule packageRule) {
        //
        if (classType == null) {
            return;
        }

        if (classType.changeWholePackageAndName(packageRule)) {
            return;
        }

        classType.changeName(nameRule);
        classType.changePackage(packageRule);
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

    public void setPackageName(String packageName) {
        //
        classType.setPackageName(packageName);
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
                    || name.toUpperCase().equals("INTEGER")
                    || name.toUpperCase().equals("SHORT")
                    || name.toUpperCase().equals("LONG")
                    || name.toUpperCase().equals("DOUBLE")
                    || name.toUpperCase().equals("FLOAT")
                    || name.toUpperCase().equals("CHAR")
                    || name.toUpperCase().equals("BOOLEAN")
                    || name.toUpperCase().equals("STRING")
                    || name.toUpperCase().equals("BYTE")
                    ) {
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
                if (returnType.hasTypeArgument()) {
                    for (ClassType typeArgument : returnType.getTypeArguments()) {
                        classNames.add(typeArgument.getClassName());
                    }
                }
            }
            for (ParameterModel parameterModel : methodModel.getParameterModels()) {
                ClassType parameterType = parameterModel.getType();
                classNames.add(parameterType.getClassName());
                if (parameterType.hasTypeArgument()) {
                    for (ClassType typeArgument : parameterType.getTypeArguments()) {
                        classNames.add(typeArgument.getClassName());
                    }
                }
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

    public Comment getNodeComment() {
        return nodeComment;
    }

    public void setNodeComment(Comment nodeComment) {
        this.nodeComment = nodeComment;
    }

    public Comment getTypeComment() {
        return typeComment;
    }

    public void setTypeComment(Comment typeComment) {
        this.typeComment = typeComment;
    }

    public String toJson() {
        //
        return JsonUtil.toJson(this);
    }
}
