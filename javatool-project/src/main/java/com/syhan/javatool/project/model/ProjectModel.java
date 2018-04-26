package com.syhan.javatool.project.model;

public class ProjectModel {
    //
    private String name;
    private String groupId;

    private ProjectModel parent;

    public ProjectModel(String name, String groupId) {
        this.name = name;
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ProjectModel getParent() {
        return parent;
    }

    public void setParent(ProjectModel parent) {
        this.parent = parent;
    }
}
