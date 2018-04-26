package com.syhan.javatool.analyzer.viewer;

import com.syhan.javatool.analyzer.entity.JavaDependency;

import java.util.List;

public class DependencyViewer {
    //
    private ModuleNode root;

    public DependencyViewer(List<JavaDependency> modules) {
        this.root = new ModuleNode("");
        for (JavaDependency module : modules) {
            this.root.add(module.getToModule());
        }
    }

    public String show() {
        //
        return root.show("");
    }

    @Override
    public String toString() {
        //
        return root.toString();
    }
}