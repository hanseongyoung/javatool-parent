package com.syhan.javatool.project.model;

public class ProjectModel {
    //
    private String name;
    private String groupId;
    private String version;

    private ProjectModel parent;

    public ProjectModel(String name, String groupId, String version) {
        this.name = name;
        this.groupId = groupId;
        this.version = version;
    }

    public boolean hasParent() {
        //
        return (parent != null);
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
