package com.syhan.javatool.generator.model;

import com.github.javaparser.ast.comments.Comment;

import java.util.ArrayList;
import java.util.List;

public class MethodModel {
    //
    private String name;
    private String access;
    private ClassType returnType;
    private List<ParameterModel> parameterModels;
    private Comment comment;

    public MethodModel(String name, ClassType returnType) {
        //
        this.name = name;
        this.returnType = returnType;
        this.parameterModels = new ArrayList<>();
    }

    public MethodModel(MethodModel other) {
        //
        this.name = other.name;
        this.access = other.access;
        this.returnType = ClassType.copyOf(other.returnType);
        this.parameterModels = new ArrayList<>();
        for (ParameterModel parameterModel : other.parameterModels) {
            this.parameterModels.add(new ParameterModel(parameterModel));
        }
        this.comment = other.comment;
    }

    public int parameterSize() {
        //
        return parameterModels.size();
    }

    public void addParameterModel(ParameterModel parameterModel) {
        //
        this.parameterModels.add(parameterModel);
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

    public List<ParameterModel> getParameterModels() {
        return parameterModels;
    }

    public void setParameterModels(List<ParameterModel> parameterModels) {
        this.parameterModels = parameterModels;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
