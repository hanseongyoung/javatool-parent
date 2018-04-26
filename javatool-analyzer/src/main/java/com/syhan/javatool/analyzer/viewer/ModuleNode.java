package com.syhan.javatool.analyzer.viewer;

import java.util.ArrayList;
import java.util.List;

public class ModuleNode {
    //
    private String name;
    private List<ModuleNode> children;
    private ModuleNode parent;

    public ModuleNode(String name) {
        //
        this(name, null);
    }

    public ModuleNode(String name, ModuleNode parent) {
        //
        this.name = name;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public void add(String addName) {
        // childName : com.foo.bar
        int dotIndex = addName.indexOf(".");
        if (dotIndex <= 0) {
            checkAndNewChild(addName);
            return;
        }

        String parentName = addName.substring(0, dotIndex);
        String childName = addName.substring(dotIndex + 1);
        ModuleNode childNode =  checkAndNewChild(parentName);
        childNode.add(childName);
    }

    private ModuleNode checkAndNewChild(String nodeName) {
        //
        ModuleNode moduleNode = find(nodeName);
        if (moduleNode == null) {
            moduleNode = new ModuleNode(nodeName, this);
            this.children.add(moduleNode);
        }
        return moduleNode;
    }

    private ModuleNode find(String nodeName) {
        //
        for (ModuleNode moduleNode : children) {
            if (moduleNode.name.equals(nodeName)) {
                return moduleNode;
            }
        }
        return null;
    }

    public boolean isRoot() {
        //
        return this.parent == null;
    }

    public String getName() {
        return name;
    }

    public List<ModuleNode> getChildren() {
        return children;
    }

    public ModuleNode getParent() {
        return parent;
    }

    public String show(String prefix) {
        //
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(prefix).append("+ ").append(name);
        for (ModuleNode child : children) {
            sb.append(child.show(prefix + "    "));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n +").append(this.name);
        for (ModuleNode child : children) {
            sb.append(child.toString());
        }
        return sb.toString();
    }
}