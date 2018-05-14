package com.syhan.javatool.project.model;

import com.sun.tools.javac.util.Assert;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectModel {
    //
    private String name;
    private String groupId;
    private String version;
    private String packaging;

    private ProjectModel parent;
    private List<ProjectModel> children;

    private String workspacePath;

    public ProjectModel(String name, String groupId, String version) {
        //
        this(name, groupId, version, null);
    }

    public ProjectModel(String name, String groupId, String version, String packaging) {
        //
        this.name = name;
        this.groupId = groupId;
        this.version = version;
        this.packaging = packaging;
        this.children = new ArrayList<>();
    }

    public ProjectConfiguration configuration(ConfigurationType configurationType) {
        //
        Assert.checkNonNull(workspacePath);
        return new ProjectConfiguration(configurationType, workspacePath, name);
    }

    public ProjectModel findBySuffix(String suffix) {
        //
        if (this.name.endsWith(suffix)) {
            return this;
        }

        for (ProjectModel child : children) {
            ProjectModel finded = child.findBySuffix(suffix);
            if (finded != null) {
                return finded;
            }
        }
        return null;
    }

    public boolean isPom() {
        //
        return "pom".equalsIgnoreCase(packaging);
    }

    public boolean hasParent() {
        //
        return (parent != null);
    }

    public void add(ProjectModel child) {
        //
        child.setParent(this);
        if (this.workspacePath != null) {
            child.workspacePath = this.workspacePath + File.separator + this.name;
        }
        this.children.add(child);
    }

    public boolean hasChildren() {
        //
        return children != null && children.size() > 0;
    }

    public List<ProjectModel> getChildren() {
        return children;
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

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public void setWorkspacePath(String workspacePath) {
        this.workspacePath = workspacePath;
    }
}
