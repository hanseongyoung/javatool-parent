package com.syhan.javatool.project.model;

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
    private List<Dependency> dependencies;

    private String workspacePath;

    public ProjectModel(String name, String groupId) {
        //
        this(name, groupId, null, null);
    }

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
        this.dependencies = new ArrayList<>();
    }

    public ProjectConfiguration configuration(ConfigurationType configurationType) {
        //
        assert workspacePath != null : "The workspacePath can't be null.";
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

    public boolean isRoot() {
        //
        return (parent == null);
    }

    public ProjectModel add(ProjectModel child) {
        //
        child.setParent(this);
        if (this.workspacePath != null) {
            child.workspacePath = this.workspacePath + File.separator + this.name;
        }
        this.children.add(child);
        return this;
    }

    public boolean hasChildren() {
        //
        return children != null && children.size() > 0;
    }

    public ProjectModel addDependency(Dependency dependency) {
        //
        this.dependencies.add(dependency);
        return this;
    }

    public ProjectModel addDependency(ProjectModel dependencyProject) {
        //
        this.dependencies.add(new Dependency(dependencyProject.groupId, dependencyProject.name, "${project.version}"));
        return this;
    }

    public ProjectModel addDependencies(List<ProjectModel> dependencyProjects) {
        //
        if (dependencyProjects == null) {
            return this;
        }

        dependencyProjects.forEach(dependencyProject -> this.addDependency(dependencyProject));
        return this;
    }

    public ProjectModel addDependency(String groupId, String name) {
        //
        this.dependencies.add(new Dependency(groupId, name));
        return this;
    }

    public ProjectModel addDependency(String groupId, String name, String version) {
        //
        this.dependencies.add(new Dependency(groupId, name, version));
        return this;
    }

    public boolean hasDependencies() {
        //
        return dependencies != null && dependencies.size() > 0;
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

    public ProjectModel setWorkspacePath(String workspacePath) {
        this.workspacePath = workspacePath;
        return this;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }
}
