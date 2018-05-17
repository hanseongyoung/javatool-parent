package com.syhan.javatool.project.model;

public class Dependency {
    //
    private String groupId;
    private String name;
    private String version;
    private String type;
    private String scope;

    public Dependency(String groupId, String name) {
        //
        this(groupId, name, null, null, null);
    }

    public Dependency(String groupId, String name, String version) {
        //
        this(groupId, name, version, null, null);
    }

    public Dependency(String groupId, String name, String version, String type, String scope) {
        //
        this.groupId = groupId;
        this.name = name;
        this.version = version;
        this.type = type;
        this.scope = scope;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
